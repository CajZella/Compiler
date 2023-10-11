package ir.instrs;

import ir.BasicBlock;
import ir.types.Type;

/*
    <result> = trunc <ty> <value> to <ty2>
    将ty的value的type缩减为ty2
 */
public class Trunc extends Instr {
    public Trunc(int num, Type type, BasicBlock pBB) {
        super(ValueType.trunc, String.format("%%d", num), type, pBB);
    }
    @Override
    public String toString() {
        return String.format("%s = trunc %s %s to %s", name, getOperand(0).getType(), getOperand(0).getName(), type);
    }
}
