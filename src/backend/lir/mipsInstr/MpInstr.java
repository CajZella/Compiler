package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import util.MyLinkedNode;

public abstract class MpInstr extends MyLinkedNode {
    public enum MipsInstrType {
        addu,
        addiu,
        and, // rd = rs and rt
        andi,
        nor,
        or,
        ori,
        subu,
        xor,
        mult, //todo:窥孔优化 peephole
        div,

        sll,
        sra,
        srl,

        seq,
        sne,
        slt,
        sle,

        lw,
        sw,

        beq, // if(rs=rt) then PC+offset
        bge, // if(rs>=0) then PC+offset
        bgt, // if(rs>0)
        ble, // if(rs<=0)
        blt,
        bne, // if (rs!=rt)

        j,
        jal,
        jalr,
        jr,

        syscall,
        li,
        la,
        move,

        comment,
    }
    protected MipsInstrType instrType;
    protected MpBlock block;
    public MpInstr(MipsInstrType instrType, MpBlock block) {
        this.instrType = instrType;
        this.block = block;
        this.block.addMpBlock(this);
    }
    public MpBlock getBlock() { return this.block; }
}
