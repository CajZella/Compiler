package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpStore extends MpInstr {
    private MpData base;
    private MpImm offset = null;
    public MpStore(MpBlock block, MpReg targetReg, MpReg base, MpImm offset) {
        super(MipsInstrType.sw, block);
        replaceSrc1(targetReg);
        replaceSrc2(base);
        this.offset = offset;
    }
    public MpStore(MpBlock block, MpReg targetReg, MpData base) {
        super(MipsInstrType.sw, block);
        replaceSrc1(targetReg);
        this.base = base;
    }
    public MpImm getOffset() { return offset; }
    public MpData getBase() { return base; }
    public String toString() {
        return null == offset ?
                String.format("%s %s, %s", instrType.toString(), src1Reg, base) :
                String.format("%s %s, %s(%s)", instrType.toString(), src1Reg, offset, src2Reg);
    }
}
