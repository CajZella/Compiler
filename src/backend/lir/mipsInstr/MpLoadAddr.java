package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpLoadAddr extends MpInstr {
    private MpData data = null;
    private MpImm offset = null;
    public MpLoadAddr(MpBlock block, MpReg targetReg, MpData data, MpReg src1, MpImm offset) {
        super(MipsInstrType.la, block);
        replaceDst(targetReg);
        if (null != data) this.data = data;
        if (null != src1) replaceSrc1(src1);
        if (null != offset && offset.getVal() != 0) this.offset = offset;
    }
    public String toString() {
        if (null != data && null != src1Reg && null != offset)
            return String.format("%s %s, %s+%s(%s)", instrType, dstReg, data, offset, src1Reg);
        else if (null == data && null != src1Reg && null != offset)
            return String.format("%s %s, %s(%s)", instrType, dstReg, offset, src1Reg);
        else if (null != data && null != src1Reg && null == offset)
            return String.format("%s %s, %s(%s)", instrType, dstReg, data, src1Reg);
        else if (null != data && null == src1Reg && null != offset)
            return String.format("%s %s, %s+%s", instrType, dstReg, data, offset);
        else if (null != data && null == src1Reg && null == offset)
            return String.format("%s %s, %s", instrType, dstReg, data);
        else if (null == data && null != src1Reg && null == offset)
            return String.format("%s %s, (%s)", instrType, dstReg, src1Reg);
        else return null;
    }
}
