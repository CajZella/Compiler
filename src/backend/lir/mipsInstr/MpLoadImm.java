package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpLoadImm extends MpInstr {
    private MpImm imm;
    public MpLoadImm(MpBlock block, MpReg targetReg, MpImm imm) {
        super(MipsInstrType.li, block);
        replaceDst(targetReg);
        this.imm = imm;
    }
    public String toString() {
        return String.format("%s %s, %s", instrType, dstReg, imm);
    }
}
