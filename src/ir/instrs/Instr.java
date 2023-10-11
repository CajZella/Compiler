package ir.instrs;

import ir.BasicBlock;
import ir.User;
import ir.types.Type;

public abstract class Instr extends User {
    private BasicBlock pBB;
    public Instr(ValueType valueTy, String name, Type type, BasicBlock pBB) {
        super(valueTy, name, type);
        this.pBB = pBB;
    }
    public BasicBlock getParent() { return this.pBB; }
    public boolean mayWriteToMemory() { return this.valueTy == ValueType.call || this.valueTy == ValueType.store; }
    public boolean mayReadFromMemory() { return this.valueTy == ValueType.load; }
    public boolean isTerminator() { return this.valueTy == ValueType.br || this.valueTy == ValueType.ret; }
    @Override
    public abstract String toString();
}
