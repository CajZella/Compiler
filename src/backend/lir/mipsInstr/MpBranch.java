package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

public class MpBranch extends MpInstr {
    private MpReg sourceReg;
    private MpReg targetReg;
    private MpImm offset;
    public MpBranch(MipsInstrType type, MpBlock block, MpReg sourceReg, MpReg targetReg, MpImm offset) {
        super(type, block);
        this.sourceReg = sourceReg;
        this.targetReg = targetReg;
        this.offset = offset;
    }
    public MpBranch(MipsInstrType type, MpBlock block, MpReg sourceReg, MpImm offset) {
        super(type, block);
        this.sourceReg = sourceReg;
        this.offset = offset;
    }
    public String toString() {
        return instrType == MipsInstrType.beq || instrType == MipsInstrType.bne
                ? String.format("%s %s, %s, %s", instrType, sourceReg, targetReg, offset)
                : String.format("%s %s, %s", instrType, sourceReg, offset);
    }
}
