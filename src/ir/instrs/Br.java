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
        if (operandsSize() == 1) {
            pBB.addSuccBB((BasicBlock) getOperand(0));
            ((BasicBlock) getOperand(0)).addPrecBB(pBB);
        } else {
            pBB.addSuccBB((BasicBlock) getOperand(1));
            pBB.addSuccBB((BasicBlock) getOperand(2));
            ((BasicBlock) getOperand(1)).addPrecBB(pBB);
            ((BasicBlock) getOperand(2)).addPrecBB(pBB);
        }
    }
    public boolean isCondBr() { return operandsSize() != 1; }
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
