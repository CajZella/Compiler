package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpBranch extends MpInstr {
    private MpReg sourceReg;
    private MpOpd target;
    private MpLabel label;
    public MpBranch(MipsInstrType type, MpBlock block, MpReg sourceReg, MpOpd target, MpLabel label) {
        super(type, block);
        replaceSrc1(sourceReg);
        if (target instanceof MpReg)
            replaceSrc2((MpReg) target);
        else this.target = target;
        this.label = label;
    }
    public void replaceSrc1(MpReg reg) {
        addUseReg(sourceReg, reg);
        sourceReg = reg;
    }
    public void replaceSrc2(MpReg reg) {
        if (target instanceof MpReg) {
            addUseReg((MpReg) target, reg);
            target = reg;
        }
    }
    public String toString() {
        return String.format("%s %s, %s, %s", instrType, sourceReg, target, label);
    }
}
