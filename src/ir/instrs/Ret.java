package ir.instrs;

import ir.BasicBlock;

public class Ret extends Instr {
    public Ret(BasicBlock pBB) {
        super(ValueType.ret, null, null, pBB);
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
