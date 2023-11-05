package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpAlu extends MpInstr {
    private MpReg destReg = null; // rd
    private MpReg sourceReg = null; // rs
    private MpReg targetReg = null; // rt
    private MpImm imm = null; // immediate
    private boolean isRI;
    public MpAlu(MipsInstrType type, MpBlock block, MpReg rd, MpReg rs, MpReg rt) {
        super(type, block);
        replaceDst(rd);
        replaceSrc1(rs);
        replaceSrc2(rt);
        this.isRI = false;
    }
    public MpAlu(MipsInstrType type, MpBlock block, MpReg rd, MpReg rs, MpImm imm) {
        super(type, block);
        replaceDst(rd);
        replaceSrc1(rs);
        this.imm = imm;
        this.isRI = true;
    }
    public MpAlu(MipsInstrType type, MpBlock block, MpReg rs, MpReg rt) {
        super(type, block);
        replaceSrc1(rs);
        replaceSrc2(rt);
        this.isRI = false;
    }
    public MpAlu(MipsInstrType type, MpBlock block, MpReg rs, MpImm imm) {
        super(type, block);
        replaceSrc1(rs);
        this.imm = imm;
        this.isRI = true;
    }
    public void replaceDst(MpReg reg) {
        addDefReg(destReg, reg);
        destReg = reg;
    }
    public void replaceSrc1(MpReg reg) {
        addUseReg(sourceReg, reg);
        sourceReg = reg;
    }
    public void replaceSrc2(MpReg reg) {
        addUseReg(targetReg, reg);
        targetReg = reg;
    }
    public String toString() {
        return null == destReg ?
                (isRI ?
                        String.format("%s %s, %s", instrType, sourceReg, imm) :
                        String.format("%s %s, %s", instrType, sourceReg, targetReg)) :
                (isRI ?
                        String.format("%s %s, %s, %s", instrType, destReg, sourceReg, imm) :
                        String.format("%s %s, %s, %s", instrType, destReg, sourceReg, targetReg));
    }
}
