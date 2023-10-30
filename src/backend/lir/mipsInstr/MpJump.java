package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

public class MpJump extends MpInstr {
    private MpLabel label;
    private MpReg sourceReg;
    public MpJump(MipsInstrType type, MpBlock block, MpLabel label) {
        super(type, block);
        this.label = label;
    }
    public MpJump(MipsInstrType type, MpBlock block, MpReg sourceReg) {
        super(type, block);
        this.sourceReg = sourceReg;
    }
    public String toString() {
        return instrType == MipsInstrType.jr
                ? String.format("%s %s", instrType, sourceReg)
                : String.format("%s %s", instrType, label);
    }
}
