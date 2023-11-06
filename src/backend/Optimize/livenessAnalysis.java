package backend.Optimize;

import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsOperand.MpReg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/*
 * 全局活跃变量分析
 */
public class livenessAnalysis {
    private MpFunction curMF;
    private HashMap<MpBlock, HashSet<MpReg>> def = new HashMap<>();
    private HashMap<MpBlock, HashSet<MpReg>> use = new HashMap<>();
    private HashMap<MpBlock, HashSet<MpReg>> in = new HashMap<>();
    private HashMap<MpBlock, HashSet<MpReg>> out = new HashMap<>();
    public livenessAnalysis(MpFunction curMF) {
        this.curMF = curMF;
        analysisBlocksLive();
        while(!analysisOnce());
    }

    public HashMap<MpBlock, HashSet<MpReg>> getIn() { return in; }

    public HashMap<MpBlock, HashSet<MpReg>> getOut() { return out; }

    private void analysisBlocksLive() {
        for (MpBlock curMB : curMF.getMpBlocks()) {
            HashSet curDef = new HashSet();
            HashSet curUse = new HashSet();
            def.put(curMB, curDef);
            use.put(curMB, curUse);
            for (MpInstr curMI : curMB.getMpInstrs()) {
                HashSet<MpReg> useRegs = curMI.getUseRegs();
                for (MpReg curReg : useRegs)
                    if (!curDef.contains(curReg))
                        curUse.add(curReg);
                HashSet<MpReg> defRegs = curMI.getDefRegs();
                for (MpReg curReg : defRegs)
                    curDef.add(curReg);
            }
        }
    }
    private boolean analysisOnce() {
        boolean changed = false;
        HashMap<MpBlock, Boolean> visitedMBs = new HashMap<>();
        LinkedList<MpBlock> MBs = new LinkedList<>();
        // init
        for (MpBlock curMB : curMF.getMpBlocks()) {
            visitedMBs.put(curMB, false);
            if (curMB.getSuccMBs().size() == 0)
                MBs.add(curMB);
        }
        while (!MBs.isEmpty()) {
            MpBlock curMB = MBs.poll();
            if (visitedMBs.get(curMB)) continue;
            visitedMBs.put(curMB, true);
            HashSet<MpReg> curDef = def.get(curMB);
            HashSet<MpReg> curUse = use.get(curMB);
            HashSet<MpReg> curIn = new HashSet<>();
            HashSet<MpReg> curOut = new HashSet<>();
            in.put(curMB, curIn);
            out.put(curMB, curOut);
            for (MpBlock curSuccMB : curMB.getSuccMBs()) {
                HashSet<MpReg> curSuccIn = in.get(curSuccMB);
                if (!equal(curOut, curSuccIn)) {
                    changed = true;
                    curOut.addAll(curSuccIn);
                }
            }
            curIn.addAll(curOut);
            curIn.removeAll(curDef);
            curIn.addAll(curUse);
            for (MpBlock curPrecMB : curMB.getPrecMBs())
                if (!visitedMBs.get(curPrecMB))
                    MBs.add(curPrecMB);
        }
        return changed;
    }
    public boolean equal(HashSet<MpReg> a, HashSet<MpReg> b) {
        if (a.size() != b.size())
            return false;
        for (MpReg curReg : a)
            if (!b.contains(curReg))
                return false;
        return true;
    }
}
