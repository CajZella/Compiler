package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.IntegerType;

// add, sub, mul, sdiv, and, or, srem
public class Alu extends Instr {
    public Alu(ValueType valueTy, IntegerType type, BasicBlock pBB, Value...operands) {
        super(valueTy, type, pBB, operands);
    }
    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s", name, valueTy, getOperand(0).getType(), getOperand(0).getName(), getOperand(1).getName());
    }
}
