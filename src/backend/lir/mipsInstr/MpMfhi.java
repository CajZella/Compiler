package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;

public class MpMfhi extends MpInstr {
    public MpMfhi(MpBlock block, MpReg rd) {
        super(MipsInstrType.mfhi, block);
        replaceDst(rd);
    }
    public String toString() { return String.format("%s %s", instrType, dstReg); }
}
