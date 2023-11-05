package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.IntegerType;

public class Icmp extends Instr {
    public enum IcmpOp {
        eq, ne, sgt, sge, slt, sle,
    }
    private IcmpOp op;
    public Icmp(IcmpOp op, BasicBlock pBB, Value...operands) {
        super(ValueType.icmp, new IntegerType(1), pBB, operands);
        this.op = op;
    }
    public IcmpOp getOp() { return op; }
    @Override
    public String toString() {
        return String.format("%s = icmp %s %s %s, %s", name, op, getOperand(0).getType(), getOperand(0).getName(), getOperand(1).getName());
    }
}
