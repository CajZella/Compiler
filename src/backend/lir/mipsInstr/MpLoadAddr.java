package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpReg;

public class MpLoadAddr extends MpInstr {
    private MpData data;
    private MpReg targetReg;
    public MpLoadAddr(MpBlock block, MpReg targetReg, MpData data) {
        super(MipsInstrType.la, block);
        this.targetReg = targetReg;
        this.data = data;
    }
    public String toString() {
        return String.format("%s %s, %s", instrType, targetReg, data);
    }
}
