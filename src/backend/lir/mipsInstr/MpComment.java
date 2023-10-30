package backend.lir.mipsInstr;

import backend.lir.MpBlock;

public class MpComment extends MpInstr {
    private String comment;
    public MpComment(MpBlock block, String comment) {
        super(MipsInstrType.comment, block);
        this.comment = comment;
    }
    public String toString() {
        return "#" + comment;
    }
}
