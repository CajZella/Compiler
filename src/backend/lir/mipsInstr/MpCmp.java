package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

public class MpCmp extends MpInstr {
    private MpReg destReg;
    private MpReg sourceReg;
    private MpReg targetReg;
    private MpImm imm;
    private boolean isRI;
    public MpCmp(MipsInstrType type, MpBlock block, MpReg destReg, MpReg sourceReg, MpReg targetReg) {
        super(type, block);
        this.destReg = destReg;
        this.sourceReg = sourceReg;
        this.targetReg = targetReg;
        this.isRI = false;
    }
    public MpCmp(MipsInstrType type, MpBlock block, MpReg sourceReg, MpReg targetReg, MpImm imm) {
        super(type, block);
        this.sourceReg = sourceReg;
        this.targetReg = targetReg;
        this.imm = imm;
        this.isRI = true;
    }
    public String toString() {
        return isRI
                ? String.format("%s %s, %s, %s", instrType, targetReg, sourceReg, imm)
                : String.format("%s %s, %s, %s", instrType, destReg, sourceReg, targetReg);
    }
}
