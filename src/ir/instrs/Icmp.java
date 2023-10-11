package ir.instrs;

import ir.BasicBlock;
import ir.types.IntegerType;

public class Icmp extends Instr {
    public enum IcmpOp {
        eq, ne, sgt, sge, slt, sle,
    }
    private IcmpOp op;
    public Icmp(int num, IntegerType type, BasicBlock pBB, IcmpOp op) {
        super(ValueType.icmp, String.format("%%d", num), type, pBB);
        this.op = op;
    }
    @Override
    public String toString() {
        return String.format("%s = icmp %s %s %s, %s", name, op, getOperand(0).getType(), getOperand(0), getOperand(1));
    }
}
