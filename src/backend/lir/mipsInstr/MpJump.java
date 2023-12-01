package backend.lir.mipsInstr;

import backend.BackEnd;
import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpJump extends MpInstr {
    private MpLabel label = null;
    private int K = 0;
    private int argNum = 0;
    public MpJump(MipsInstrType type, MpBlock block, MpLabel label) {
        super(type, block);
        this.label = label;
    }
    public MpJump(MpBlock block, MpLabel label, int K, int argNum) {
        super(MipsInstrType.jal, block);
        this.label = label;
        this.K = K;
        this.argNum = argNum;
        replaceDst(BackEnd.mipsPhyRegs.get(2));

    }
    public MpJump(MipsInstrType type, MpBlock block, MpReg sourceReg) {
        super(type, block);
        replaceSrc1(sourceReg);
    }
    public void dealJal() {
        int t = -1;
        for (int i = 15; i > 15 - K; i--) {
            MpInstr instr = new MpStore(block, BackEnd.mipsPhyRegs.get(i), BackEnd.mipsPhyRegs.get(27), new MpImm(t * 4));
            this.insertBefore(instr);
            t--;
        }
        this.insertBefore(new MpAlu(MipsInstrType.addiu, block, BackEnd.mipsPhyRegs.get(27), BackEnd.mipsPhyRegs.get(27), new MpImm(-4 * (K + argNum))));
        t = -1;
        for (int i = 15; i > 15 -K; i--) {
            this.insertAfter(new MpLoad(block, BackEnd.mipsPhyRegs.get(i), BackEnd.mipsPhyRegs.get(27), new MpImm(t * 4)));
            t--;
        }
        this.insertAfter(new MpAlu(MipsInstrType.addiu, block, BackEnd.mipsPhyRegs.get(27), BackEnd.mipsPhyRegs.get(27), new MpImm(4 * (K + argNum))));
    }

    public MpLabel getLabel() { return label; }
    public String toString() {
        return instrType == MipsInstrType.jr
                ? String.format("%s %s", instrType, src1Reg)
                : String.format("%s %s", instrType, label);
    }
}
