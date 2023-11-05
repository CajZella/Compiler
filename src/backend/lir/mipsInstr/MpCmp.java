package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpCmp extends MpInstr {
    private MpReg destReg;
    private MpReg sourceReg;
    private MpOpd target;
    public MpReg getDestReg() { return this.destReg; }
    public MpReg getSourceReg() { return this.sourceReg; }
    public MpOpd getTarget() { return this.target; }
    public MpCmp(MipsInstrType type, MpBlock block, MpReg destReg, MpReg sourceReg, MpOpd target) {
        super(type, block);
        replaceDst(destReg);
        replaceSrc1(sourceReg);
        if (target instanceof MpReg)
            replaceSrc2((MpReg) target);
        else
            this.target = target;
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
        addUseReg((MpReg) target, reg);
        target = reg;
    }
    public void replaceReg(MpReg oldReg, MpReg newReg) {
        if (destReg == oldReg) {
            addDefReg(oldReg, newReg);
            destReg = newReg;
        }
        if (sourceReg == oldReg) {
            addUseReg(oldReg, newReg);
            sourceReg = newReg;
        }
        if (target == oldReg) {
            addUseReg(oldReg, newReg);
            target = newReg;
        }
    }
    public String toString() {
        return String.format("%s %s, %s, %s", instrType, destReg, sourceReg, target);
    }
}
