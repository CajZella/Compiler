package ir.instrs;

import ir.BasicBlock;
import ir.types.IntegerType;

// add, sub, mul, sdiv, and, or
public class Alu extends Instr {
    public Alu(ValueType valueTy, int num, IntegerType type, BasicBlock pBB) {
        super(valueTy, String.format("%%d", num), type, pBB);
    }
    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s", name, valueTy, getOperand(0).getType(), getOperand(0), getOperand(1));
    }
}
