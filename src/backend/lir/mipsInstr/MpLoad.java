package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpLoad extends MpInstr {
    private MpOpd base;
    private MpReg targetReg = null;
    private MpImm offset = null;
    public MpLoad(MpBlock block, MpReg targetReg, MpReg base, MpImm offset) {
        super(MipsInstrType.lw, block);
        replaseDst(targetReg);
        replaceSrc(base);
        this.offset = offset;
    }
    public MpLoad(MpBlock block, MpReg targetReg, MpData base) {
        super(MipsInstrType.lw, block);
        replaseDst(targetReg);
        this.base = base;
    }
    public void replaseDst(MpReg reg) {
        addDefReg(targetReg, reg);
        targetReg = reg;
    }
    public void replaceSrc(MpReg reg) {
        addUseReg((MpReg) base, reg);
        base = reg;
    }
    public String toString() {
        return null == offset ?
                String.format("%s %s, %s", instrType, targetReg, base) :
                String.format("%s %s, %s(%s)", instrType, targetReg, offset, base);
    }
}
