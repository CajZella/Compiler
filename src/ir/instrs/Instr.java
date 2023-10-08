package ir.instrs;

import ir.BasicBlock;
import ir.User;

public class Instr extends User {
    public static enum Opcode {
        add,
        sub,
        mul,
        sdiv,
        icmp,
        and,
        or,
        call,
        alloca,
        load,
        store,
        getelementptr,
        phi,
        zext,
        br,
        ret,
    }
    private Opcode opcode; // 指令类型
    private BasicBlock pBB;

    public BasicBlock getParent() { return this.pBB; }

    public boolean mayWriteToMemory() { return this.opcode == Opcode.call || this.opcode == Opcode.store; }

    public Opcode getOpcode() { return this.opcode; }

    // clone一个没有parent和name的instruction返回
    //@Override
    //public Instr clone() {}
}
