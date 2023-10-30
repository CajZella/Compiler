package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;

public class MpMove extends MpInstr {
    private MpReg sourceReg;
    private MpReg targetReg;
    public MpMove(MpBlock block, MpReg targetReg, MpReg sourceReg) {
        super(MipsInstrType.move, block);
        this.targetReg = targetReg;
        this.sourceReg = sourceReg;
    }
    public String toString() {
        return String.format("%s %s, %s", instrType, targetReg, sourceReg);
    }
}
