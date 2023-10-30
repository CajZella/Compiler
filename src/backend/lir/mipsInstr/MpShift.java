package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

public class MpShift extends MpInstr {
    private MpReg destReg;
    private MpReg targetReg;
    private int shift;
    public MpShift(MipsInstrType type, MpBlock block, MpReg destReg, MpReg targetReg, int shift) {
        super(type, block);
        this.destReg = destReg;
        this.targetReg = targetReg;
        this.shift = shift;
    }
    public String toString() {
        return String.format("%s %s, %s, %d", instrType, destReg, targetReg, shift);
    }
}

