package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpShift extends MpInstr {
    private MpReg destReg;
    private MpReg targetReg;
    private MpImm shift;
    public MpShift(MipsInstrType type, MpBlock block, MpReg destReg, MpReg targetReg, MpImm shift) {
        super(type, block);
        replaceDst(destReg);
        replaceSrc(targetReg);
        this.shift = shift;
    }
    public void replaceDst(MpReg reg) {
        addDefReg(destReg, reg);
        destReg = reg;
    }
    public void replaceSrc(MpReg reg) {
        addUseReg(targetReg, reg);
        targetReg = reg;
    }
    public String toString() {
        return String.format("%s %s, %s, %d", instrType, destReg, targetReg, shift);
    }
}

