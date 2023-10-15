package ir.instrs;

import ir.BasicBlock;
import ir.Value;

public class Ret extends Instr {
    public Ret(BasicBlock pBB, Value operand) {
        super(ValueType.ret, null, pBB, operand);
    }
    @Override
    public String toString() {
        if (isOperandsEmpty()) {
            return "ret void";
        } else {
            return "ret " + getOperand(0).getType() + " " + getOperand(0).getName();
        }
    }
}
