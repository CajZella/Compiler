package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpLoad extends MpInstr {
    private MpData base = null;
    private MpImm offset = null;
    public MpLoad(MpBlock block, MpReg targetReg, MpReg base, MpImm offset) {
        super(MipsInstrType.lw, block);
        replaceDst(targetReg);
        replaceSrc1(base);
        this.offset = offset;
    }
    public MpLoad(MpBlock block, MpReg targetReg, MpData base) {
        super(MipsInstrType.lw, block);
        replaceDst(targetReg);
        this.base = base;
    }
    public String toString() {
        return null == offset ?
                String.format("%s %s, %s", instrType, dstReg, base) :
                String.format("%s %s, %s(%s)", instrType, dstReg, offset, src1Reg);
    }
}
