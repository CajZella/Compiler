package backend.lir.mipsInstr;

import backend.BackEnd;
import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpCmp extends MpInstr {
    private MpImm src2Imm;
    public MpCmp(MipsInstrType type, MpBlock block, MpReg destReg, MpReg sourceReg, MpOpd target) {
        super(type, block);
        replaceDst(destReg);
        replaceSrc1(sourceReg);
        if (target instanceof MpReg)
            replaceSrc2((MpReg) target);
        else
            this.src2Imm = (MpImm) target;
    }
    public MpOpd getSrc2() { return null == src2Reg ? src2Imm : src2Reg; }
    public void replaceZeroWithReg() {
        if (null == src2Imm || src2Imm.getVal() != 0)
            return;
        src2Imm = null;
        replaceSrc2(BackEnd.mipsPhyRegs.get(0));
    }
    public String toString() {
        return String.format("%s %s, %s, %s", instrType, dstReg, src1Reg, null == src2Reg ? src2Imm : src2Reg);
    }
}
