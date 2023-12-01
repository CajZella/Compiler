package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;
import util.MyLinkedList;

import java.util.ArrayList;

public class MpMove extends MpInstr {
    public MpMove(MpBlock block, MpReg targetReg, MpReg sourceReg) {
        super(MipsInstrType.move, block);
        replaceDst(targetReg);
        replaceSrc1(sourceReg);
    }
    public String toString() {
        return String.format("%s %s, %s", instrType, dstReg, src1Reg);
    }
}
