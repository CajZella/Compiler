package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

public class MpLoadImm extends MpInstr {
    private MpImm imm;
    private MpReg targetReg;
    public MpLoadImm(MpBlock block, MpReg targetReg, MpImm imm) {
        super(MipsInstrType.li, block);
        this.targetReg = targetReg;
        this.imm = imm;
    }
    public String toString() {
        return String.format("%s %s, %s", instrType, targetReg, imm);
    }
}
