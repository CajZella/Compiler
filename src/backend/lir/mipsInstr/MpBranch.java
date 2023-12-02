package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpBranch extends MpInstr {
    private MpImm src2Imm = null;
    private MpLabel label;
    public MpBranch(MipsInstrType type, MpBlock block, MpReg src1Reg, MpOpd src2, MpLabel label) {
        super(type, block);
        replaceSrc1(src1Reg);
        if (src2 instanceof MpReg)
            replaceSrc2((MpReg) src2);
        else this.src2Imm = (MpImm) src2;
        this.label = label;
    }
    public void replaceLabel(MpLabel label) { this.label = label; }
    public MpLabel getLabel() { return label; }
    public String toString() {
        return String.format("%s %s, %s, %s", instrType, src1Reg, null == src2Reg ? src2Imm : src2Reg, label);
    }
}
