package ir.instrs;

import ir.BasicBlock;
import ir.types.Type;

public class Zext extends Instr {
    public Zext(int num, Type type, BasicBlock pBB) {
        super(ValueType.zext, String.format("%%d", num), type, pBB);
    }
    @Override
    public String toString() {
        return String.format("%s = zext %s %s to %s", name, getOperand(0).getType(), getOperand(0).getName(), type);
    }
}
