package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpShift extends MpInstr {
    private MpImm shift;
    public MpShift(MipsInstrType type, MpBlock block, MpReg destReg, MpReg targetReg, MpImm shift) {
        super(type, block);
        replaceDst(destReg);
        replaceSrc1(targetReg);
        this.shift = shift;
    }
    public String toString() {
        return String.format("%s %s, %s, %s", instrType, dstReg, src1Reg, shift);
    }
}

