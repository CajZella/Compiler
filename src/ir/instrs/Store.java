package ir.instrs;

import ir.BasicBlock;
import ir.Value;

/*
    store <ty> <value>, <ty>* <pointer>
    store i32 %2, i32* %1
 */
public class Store extends Instr {
    public Store(BasicBlock pBB, Value...operands) {
        super(ValueType.store, null, pBB, operands);
    }
    public Value getValue() { return getOperand(0); }
    public Value getPointer() { return getOperand(1); }
    @Override
    public String toString() {
        return String.format("store %s %s, %s %s",
                getValue().getType(), getValue().getName(),
                getPointer().getType(), getPointer().getName());
    }
}
