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
        if (null != offset && offset.getVal() != 0) this.offset = offset;
    }
    public MpImm getOffset() { return offset; }
    public MpData getBase() { return base; }
    public MpLoad(MpBlock block, MpReg targetReg, MpData data, MpReg base, MpImm offset) {
        super(MipsInstrType.lw, block);
        if (null != targetReg) replaceDst(targetReg);
        if (null != data) this.base = data;
        if (null != base) replaceSrc1(base);
        if (null != offset && offset.getVal() != 0) this.offset = offset;
        if (null != dstReg && dstReg.getName().equals("vr17")) {
            int x = 1;
        }
    }
    public String toString() {
        // todo: 进行了特判，如果可以，后续优化下，原因:在codegen阶段拿到的function stack size并不是最终的，regalloca阶段会更新。当函数参数较多时，需要从栈中读取，如果先挪出函数栈空间，读取参数的offset必须随regalloca更新
        if (isSPreference) {
            return String.format("lw %s, %d(%s)", dstReg, block.getFunction().getStackSize() + offset.getVal(), src1Reg);
        }
        if (null != base && null != src1Reg && null != offset)
            return String.format("%s %s, %s+%s(%s)", instrType, dstReg, base, offset, src1Reg);
        else if (null == base && null != src1Reg && null != offset)
            return String.format("%s %s, %s(%s)", instrType, dstReg, offset, src1Reg);
        else if (null != base && null != src1Reg && null == offset)
            return String.format("%s %s, %s(%s)", instrType, dstReg, base, src1Reg);
        else if (null != base && null == src1Reg && null != offset)
            return String.format("%s %s, %s+%s", instrType, dstReg, base, offset);
        else if (null != base && null == src1Reg && null == offset)
            return String.format("%s %s, %s", instrType, dstReg, base);
        else if (null == base && null != src1Reg && null == offset)
            return String.format("%s %s, (%s)", instrType, dstReg, src1Reg);
        else return null;
    }
}
