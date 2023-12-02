package backend.lir.mipsInstr;

import backend.lir.MpBlock;
import backend.lir.mipsOperand.MpReg;
import util.MyLinkedNode;

import java.util.HashSet;

public abstract class MpInstr extends MyLinkedNode {
    public enum MipsInstrType {
        addu,
        addiu,
        and, // rd = rs and rt
        andi,
        or,
        ori,
        subu,
        xor,
        mul,
        mult,
        div,
        mfhi,

        sll,
        sra, // 算术右移

        seq,
        sne,
        slt,
        slti,
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
        jr,

        syscall,
        li,
        la,
        move,

        comment,
    }
    protected MipsInstrType instrType;
    protected MpBlock block;
    protected MpReg dstReg = null;
    protected MpReg src1Reg = null;
    protected MpReg src2Reg = null;
    protected boolean isSPreference = false;
    public void replaceDst(MpReg reg) {
        addDefReg(dstReg, reg);
        dstReg = reg;
    }
    public void replaceSrc1(MpReg reg) {
        addUseReg(src1Reg, reg);
        src1Reg = reg;
    }
    public void replaceSrc2(MpReg reg) {
        addUseReg(src2Reg, reg);
        src2Reg = reg;
    }
    public boolean hasDstReg() { return null != dstReg; }
    public boolean hasSrc1Reg() { return null != src1Reg; }
    public boolean hasSrc2Reg() { return null != src2Reg; }
    public MpReg getDstReg() { return dstReg; }
    public MpReg getSrc1Reg() { return src1Reg; }
    public MpReg getSrc2Reg() { return src2Reg; }
    public void setSPreference() { isSPreference = true; }
    public boolean isSPreference() { return isSPreference; }
    protected HashSet<MpReg> useRegs = new HashSet<>();
    protected HashSet<MpReg> defRegs = new HashSet<>();
    public MpInstr(MipsInstrType instrType, MpBlock block) {
        this.instrType = instrType;
        this.block = block;
    }
    public void addUseReg(MpReg old, MpReg reg) {
        if (null != old)
            removeUseReg(old);
        reg.incUseTime();
        useRegs.add(reg);
    }
    public void addDefReg(MpReg old, MpReg reg) {
        if (null != old)
            removeDefReg(old);
        reg.incUseTime();
        defRegs.add(reg);
    }
    public void removeUseReg(MpReg reg) {
        reg.decUseTime();
        useRegs.remove(reg);
    }
    public void removeDefReg(MpReg reg) {
        reg.decUseTime();
        defRegs.remove(reg);
    }
    public HashSet<MpReg> getUseRegs() { return useRegs; }
    public HashSet<MpReg> getDefRegs() { return defRegs; }
    public MipsInstrType getInstrType() { return instrType; }
    public MpBlock getBlock() { return this.block; }
    public abstract String toString();
}
