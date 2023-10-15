package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.Type;

/*
    <result> = trunc <ty> <value> to <ty2>
    将ty的value的type缩减为ty2
 */
public class Trunc extends Instr {
    public Trunc(Type type, BasicBlock pBB, Value operand) {
        super(ValueType.trunc, type, pBB, operand);
    }
    @Override
    public String toString() {
        return String.format("%s = trunc %s %s to %s", name, getOperand(0).getType(), getOperand(0).getName(), type);
    }
}
