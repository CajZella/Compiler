package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.Type;

public class Zext extends Instr {
    public Zext(Type type, BasicBlock pBB, Value operand) {
        super(ValueType.zext, type, pBB, operand);
    }
    @Override
    public String toString() {
        return String.format("%s = zext %s %s to %s", name, getOperand(0).getType(), getOperand(0).getName(), type);
    }
}
