package ir.instrs;

import ir.BasicBlock;
import ir.User;
import ir.Value;
import ir.types.Type;

public class Instr extends User {
    protected BasicBlock pBB;
    public Instr(ValueType valueTy, Type type, BasicBlock pBB, Value...operands) {
        super(valueTy,
                (valueTy == ValueType.call && type.isVoidTy()
                        || valueTy == ValueType.store
                        || valueTy == ValueType.br
                        || valueTy == ValueType.ret) ?
                        null : ("%i" + num++), type, operands);
        this.pBB = pBB;
    }
    public Instr(ValueType valueTy, String name, Type type, BasicBlock pBB, Value...operands) {
        super(valueTy, name, type, operands);
        this.pBB = pBB;
    }
    public Instr() { super("%i" + num++); }
    public BasicBlock getParent() { return this.pBB; }
    public boolean mayWriteToMemory() { return this.valueTy == ValueType.call || this.valueTy == ValueType.store; }
    public boolean mayReadFromMemory() { return this.valueTy == ValueType.load; }
    public boolean isTerminator() { return this.valueTy == ValueType.br || this.valueTy == ValueType.ret; }
}
