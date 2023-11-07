package backend.Optimize;

import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.MpModule;
import backend.lir.mipsInstr.MpAlu;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpJump;
import backend.lir.mipsInstr.MpLoad;
import backend.lir.mipsInstr.MpStore;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpPhyReg;
import backend.lir.mipsOperand.MpReg;
import backend.lir.mipsOperand.MpStackOffset;
import util.MyLinkedList;

import java.util.HashMap;
import java.util.LinkedList;

public class RegAllocaTemp {
    private LinkedList<MpReg> phyRegs = new LinkedList<>();
    private HashMap<MpReg,MpReg> vrt2phy = new HashMap<>();
    private HashMap<MpReg,MpReg> phy2vrt = new HashMap<>();
    private HashMap<MpReg, MpStackOffset> vrt2stack = new HashMap<>();
    private MpFunction curMF;
    private MpBlock curMB;
    private MpInstr curMI;
    private int stackSize;
    private int pos;
    private MpReg sp = new MpReg(MpPhyReg.$sp);
    private int K;
    public void run(MpModule mipsModule) {
        LinkedList<MpFunction> mipsFunctions = mipsModule.getMpFunctions();
        for (MpFunction mipsFunction : mipsFunctions) {
            curMF = mipsFunction;
            if (curMF.isMain())
                initMain();
            else
                initNormalFunc();

            MyLinkedList<MpBlock> mipsBlocks = mipsFunction.getMpBlocks();
            for (MpBlock mipsBlock : mipsBlocks) {
                curMB = mipsBlock;
                allocaBlock();
            }
            // 修改function的stack size
            curMF.setStackSize(stackSize);
            MpBlock tempMB = mipsBlocks.getHead();
            for (MpInstr tempMI : tempMB.getMpInstrs()) {
                if (tempMI.getInstrType() == MpInstr.MipsInstrType.addiu
                        && tempMI.getDstReg().equal(sp)) {
                    ((MpAlu)tempMI).getImm().setVal(-stackSize);
                    break;
                }
            }
            for (MpBlock mipsBlock : mipsBlocks) {
                for (MpInstr mipsInstr1 : mipsBlock.getMpInstrs())
                    if (mipsInstr1.getInstrType() == MpInstr.MipsInstrType.jr) {
                        MpInstr mipsInstr2 = (MpInstr) mipsInstr1.getPrev();
                        ((MpAlu) mipsInstr2).getImm().setVal(stackSize);
                    }
            }
        }
    }
    private void initMain() {
        phyRegs.clear();
        vrt2phy.clear();
        phy2vrt.clear();
        vrt2stack.clear();
        for (int i = 8; i <= 25; i++) {
            phyRegs.add(new MpReg(MpPhyReg.getReg(i)));
        }
        K = 18;
        pos = 0;
        stackSize = curMF.getStackSize();
    }
    private void initNormalFunc() {
        phyRegs.clear();
        vrt2phy.clear();
        phy2vrt.clear();
        vrt2stack.clear();
        for (int i = 8; i <= 15; i++) {
            phyRegs.add(new MpReg(MpPhyReg.getReg(i)));
        }
        K = 8;
        pos = 0;
        stackSize = curMF.getStackSize();
    }
    private void allocaBlock() {
        MyLinkedList<MpInstr> mipsInstrs = curMB.getMpInstrs();
        for (MpInstr mipsInstr : mipsInstrs) {
            curMI = mipsInstr;
            if (curMI.hasDstReg()) {
                MpReg vrtReg = curMI.getDstReg();
                if (vrt2phy.containsKey(vrtReg)) {
                    MpReg phyReg = vrt2phy.get(vrtReg);
                    curMI.replaceDst(phyReg);
                } else {
                    MpReg phyReg = getPhyReg();
                    vrt2phy.put(vrtReg, phyReg);
                    phy2vrt.put(phyReg, vrtReg);
                    curMI.replaceDst(phyReg);
                }
            }
            if (curMI.hasSrc1Reg()) {
                MpReg vrtReg = curMI.getSrc1Reg();
                if (vrt2phy.containsKey(vrtReg)) {
                    MpReg phyReg = vrt2phy.get(vrtReg);
                    curMI.replaceSrc1(phyReg);
                } else {
                    MpReg phyReg = getPhyReg();
                    MpStackOffset stackOffset = vrt2stack.get(vrtReg);
                    MpLoad mpLoad = new MpLoad(curMB, phyReg, sp, stackOffset.getOffset());
                    curMI.insertBefore(mpLoad);
                    curMI.replaceSrc1(phyReg);
                    vrt2stack.remove(vrtReg);
                    vrt2phy.put(vrtReg, phyReg);
                    phy2vrt.put(phyReg, vrtReg);
                }
            }
            if (curMI.hasSrc2Reg()) {
                MpReg vrtReg = curMI.getSrc2Reg();
                if (vrt2phy.containsKey(vrtReg)) {
                    MpReg phyReg = vrt2phy.get(vrtReg);
                    curMI.replaceSrc2(phyReg);
                } else {
                    MpReg phyReg = getPhyReg();
                    MpStackOffset stackOffset = vrt2stack.get(vrtReg);
                    MpLoad mpLoad = new MpLoad(curMB, phyReg, sp, stackOffset.getOffset());
                    curMI.insertBefore(mpLoad);
                    curMI.replaceSrc2(phyReg);
                    vrt2stack.remove(vrtReg);
                    vrt2phy.put(vrtReg, phyReg);
                    phy2vrt.put(phyReg, vrtReg);
                }
            }
        }
    }
    private MpReg getPhyReg() { // 获取一个空闲物理寄存器
        MpReg phyReg = phyRegs.get(pos);
        if (phy2vrt.containsKey(phyReg)) {
            MpImm imm = new MpImm(stackSize);
            MpStore mpStore = new MpStore(curMB, phyReg, sp, imm);
            curMI.insertBefore(mpStore);
            MpReg vrtReg = phy2vrt.get(phyReg);
            vrt2phy.remove(vrtReg);
            phy2vrt.remove(phyReg);
            vrt2stack.put(vrtReg, new MpStackOffset(sp, imm));
            stackSize += 4;
        }
        pos = (pos + 1) % K;
        return phyReg;
    }
}
