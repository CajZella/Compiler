package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;
import ir.instrs.Instr;

import java.util.ArrayList;

public class MpComment extends MpInstr {
    private String comment;
    private Instr irInstr;
    public MpComment(MpBlock block, String comment, Instr irInstr) {
        super(MipsInstrType.comment, block);
        this.comment = comment;
        this.irInstr = irInstr;
    }
    public String toString() {
        return "#" + comment;
    }
}
