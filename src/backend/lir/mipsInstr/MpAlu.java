package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;

import java.util.ArrayList;

public class MpAlu extends MpInstr {
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

    public MpImm getImm() { return imm; }
    public String toString() {
        return null == dstReg ?
                (isRI ?
                        String.format("%s %s, %s", instrType, src1Reg, imm) :
                        String.format("%s %s, %s", instrType, src1Reg, src2Reg)) :
                (isRI ?
                        String.format("%s %s, %s, %s", instrType, dstReg, src1Reg, imm) :
                        String.format("%s %s, %s, %s", instrType, dstReg, src1Reg, src2Reg));
    }
}
