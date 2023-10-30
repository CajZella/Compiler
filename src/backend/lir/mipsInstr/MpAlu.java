package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

public class MpAlu extends MpInstr {
    private MpReg destReg; // rd
    private MpReg sourceReg; // rs
    private MpReg targetReg; // rt
    private MpImm imm; // immediate
    private boolean isRI;
    public MpAlu(MipsInstrType type, MpBlock block, MpReg rd, MpReg rs, MpReg rt) {
        super(type, block);
        this.destReg = rd;
        this.sourceReg = rs;
        this.targetReg = rt;
        this.isRI = false;
    }
    public MpAlu(MipsInstrType type, MpBlock block, MpReg rs, MpReg rt, MpImm imm) {
        super(type, block);
        this.sourceReg = rs;
        this.targetReg = rt;
        this.imm = imm;
        this.isRI = true;
    }
    public String toString() {
        return isRI
                ? String.format("%s %s, %s, %s", instrType, targetReg, sourceReg, imm)
                : String.format("%s %s, %s, %s", instrType, destReg, sourceReg, targetReg);
    }
}
