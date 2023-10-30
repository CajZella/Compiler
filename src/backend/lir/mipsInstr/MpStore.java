package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;

public class MpStore extends MpInstr {
    private MpReg base;
    private MpReg targetReg;
    private int offset;
    public MpStore(MpBlock block, MpReg targetReg, MpReg base, int offset) {
        super(MipsInstrType.lw, block);
        this.targetReg = targetReg;
        this.base = base;
        this.offset = offset;
    }
    public String toString() {
        return String.format("%s %s, %d(%s)", instrType, targetReg, offset, base);
    }
}
