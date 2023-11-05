package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpData;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpStore extends MpInstr {
    private MpOpd base;
    private MpReg targetReg;
    private MpImm offset = null;
    public MpStore(MpBlock block, MpReg targetReg, MpReg base, MpImm offset) {
        super(MipsInstrType.lw, block);
        replaceSrc1(targetReg);
        replaceSrc2(base);
        this.offset = offset;
    }
    public MpStore(MpBlock block, MpReg targetReg, MpData base) {
        super(MipsInstrType.lw, block);
        this.targetReg = targetReg;
        this.base = base;
    }
    public void replaceSrc1(MpReg reg) {
        addUseReg(targetReg, reg);
        targetReg = reg;
    }
    public void replaceSrc2(MpReg reg) {
        addUseReg((MpReg) base, reg);
        base = reg;
    }
    public String toString() {
        return null == offset ?
                String.format("%s %s, %s", instrType.toString(), targetReg, base) :
                String.format("%s %s, %s(%s)", instrType.toString(), targetReg, offset, base);
    }
}
