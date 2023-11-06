package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpJump extends MpInstr {
    private MpLabel label = null;
    public MpJump(MipsInstrType type, MpBlock block, MpLabel label) {
        super(type, block);
        this.label = label;
    }
    public MpJump(MipsInstrType type, MpBlock block, MpReg sourceReg) {
        super(type, block);
        replaceSrc1(sourceReg);
    }
    public MpLabel getLabel() { return label; }
    public String toString() {
        return instrType == MipsInstrType.jr
                ? String.format("%s %s", instrType, src1Reg)
                : String.format("%s %s", instrType, label);
    }
}
