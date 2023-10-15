package ir.instrs;

import ir.BasicBlock;
import ir.Value;

/*
    br i1 <cond>, label <iftrue>, label <iffalse>
    br label <dest>
 */
public class Br extends Instr {
    public Br(BasicBlock pBB, Value...operands) {
        super(ValueType.br, null, pBB, operands);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (operandsSize() == 1) {
            builder.append(String.format("br label %s", getOperand(0).getName()));
        } else {
            builder.append(String.format("br i1 %s, label %s, label %s",
                    getOperand(0).getName(), getOperand(1).getName(),
                    getOperand(2).getName()));
        }
        return builder.toString();
    }
}
