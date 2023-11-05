package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpMove extends MpInstr {
    private MpReg sourceReg;
    private MpReg targetReg;
    public MpMove(MpBlock block, MpReg targetReg, MpReg sourceReg) {
        super(MipsInstrType.move, block);
        replaceDst(targetReg);
        replaceSrc(sourceReg);
    }
    public void replaceSrc(MpReg reg) {
        addUseReg(sourceReg, reg);
        sourceReg = reg;
    }
    public void replaceDst(MpReg reg) {
        addDefReg(targetReg, reg);
        targetReg = reg;
    }
    public String toString() {
        return String.format("%s %s, %s", instrType, targetReg, sourceReg);
    }
}
