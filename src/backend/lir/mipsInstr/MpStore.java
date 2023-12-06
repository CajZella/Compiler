package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpStore extends MpInstr {
    private MpData base = null;
    private MpImm offset = null;
    public MpStore(MpBlock block, MpReg targetReg, MpReg base, MpImm offset) {
        super(MipsInstrType.sw, block);
        replaceSrc1(targetReg);
        replaceSrc2(base);
        if (null != offset && offset.getVal() != 0) this.offset = offset;
    }
    public MpStore(MpBlock block, MpReg targetReg, MpData data, MpReg base, MpImm offset) {
        super(MipsInstrType.sw, block);
        if (null != targetReg) replaceSrc1(targetReg);
        if (null != data) this.base = data;
        if (null != base) replaceSrc2(base);
        if (null != offset && offset.getVal() != 0) this.offset = offset;
    }
    public MpImm getOffset() { return offset; }
    public MpData getBase() { return base; }
    public String toString() {
        if (null != base && null != src2Reg && null != offset)
            return String.format("%s %s, %s+%s(%s)", instrType, src1Reg, base, offset, src2Reg);
        else if (null == base && null != src2Reg && null != offset)
            return String.format("%s %s, %s(%s)", instrType, src1Reg, offset, src2Reg);
        else if (null != base && null != src2Reg && null == offset)
            return String.format("%s %s, %s(%s)", instrType, src1Reg, base, src2Reg);
        else if (null != base && null == src2Reg && null != offset)
            return String.format("%s %s, %s+%s", instrType, src1Reg, base, offset);
        else if (null != base && null == src2Reg && null == offset)
            return String.format("%s %s, %s", instrType, src1Reg, base);
        else if (null == base && null != src1Reg && null == offset)
            return String.format("%s %s, (%s)", instrType, src1Reg, src2Reg);
        else return null;
    }
}
