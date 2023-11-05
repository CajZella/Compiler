package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;
import util.MyLinkedNode;

import java.util.ArrayList;
import java.util.HashSet;

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
        mul, //todo:窥孔优化 peephole
        div,
        mfhi,

        sll,
        sra,
        srl,

        seq,
        sne,
        slt,
        sle,
        sgt,
        sge,

        lw,
        sw,

        beq, // if(rs=rt) then PC+offset
        beqz,
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
    protected HashSet<MpReg> useRegs = new HashSet<>();
    protected HashSet<MpReg> defRegs = new HashSet<>();
    public MpInstr(MipsInstrType instrType, MpBlock block) {
        this.instrType = instrType;
        this.block = block;
        this.block.addMpBlock(this);
    }
    public void addUseReg(MpReg old, MpReg reg) {
        if (null != old)
            removeUseReg(old);
        useRegs.add(reg);
    }
    public void addDefReg(MpReg old, MpReg reg) {
        if (null != old)
            removeDefReg(old);
        defRegs.add(reg);
    }
    public void removeUseReg(MpReg reg) { useRegs.remove(reg); }
    public void removeDefReg(MpReg reg) { defRegs.remove(reg); }
    public HashSet<MpReg> getUseRegs() { return useRegs; }
    public HashSet<MpReg> getDefRegs() { return defRegs; }
    public MipsInstrType getInstrType() { return instrType; }
    public MpBlock getBlock() { return this.block; }
    public abstract String toString();
}
