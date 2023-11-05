package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpJump extends MpInstr {
    private MpLabel label = null;
    private MpReg sourceReg = null;
    public MpJump(MipsInstrType type, MpBlock block, MpLabel label) {
        super(type, block);
        this.label = label;
    }
    public MpJump(MipsInstrType type, MpBlock block, MpReg sourceReg) {
        super(type, block);
        replaceSrc(sourceReg);
    }
    public void replaceSrc(MpReg Reg) {
        addUseReg(sourceReg, Reg);
        sourceReg = Reg;
    }
    public String toString() {
        return instrType == MipsInstrType.jr
                ? String.format("%s %s", instrType, sourceReg)
                : String.format("%s %s", instrType, label);
    }
}
