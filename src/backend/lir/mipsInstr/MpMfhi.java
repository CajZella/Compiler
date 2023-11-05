package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpMfhi extends MpInstr {
    private MpReg destReg;
    public MpMfhi(MpBlock block, MpReg rd) {
        super(MipsInstrType.mfhi, block);
        replaceDst(rd);
    }
    public void replaceDst(MpReg reg) {
        addDefReg(destReg, reg);
        destReg = reg;
    }
    public String toString() { return String.format("%s %s", instrType, destReg); }
}
