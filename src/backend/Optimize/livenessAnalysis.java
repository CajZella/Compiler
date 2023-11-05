package backend.Optimize;

import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    }
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
    public void analysis() {
        analysisBlocksLive();

    }
}
