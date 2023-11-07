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
        // todo: 进行了特判，如果可以，后续优化下，原因:在codegen阶段拿到的function stack size并不是最终的，regalloca阶段会更新。当函数参数较多时，需要从栈中读取，如果先挪出函数栈空间，读取参数的offset必须随regalloca更新
        if (isSPreference) {
            return String.format("lw %s, %d(%s)", dstReg, block.getFunction().getStackSize() + offset.getVal(), src1Reg);
        }
        return null == offset ?
                String.format("%s %s, %s", instrType, dstReg, base) :
                String.format("%s %s, %s(%s)", instrType, dstReg, offset, src1Reg);
    }
}
