package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

public class MpLoad extends MpInstr {
    private MpReg base;
    private MpReg targetReg;
    private int offset;
    public MpLoad(MpBlock block, MpReg targetReg, MpReg base, int offset) {
        super(MipsInstrType.lw, block);
        this.targetReg = targetReg;
        this.base = base;
        this.offset = offset;
    }
    public String toString() {
        return String.format("%s %s, %d(%s)", instrType, targetReg, offset, base);
    }
}
