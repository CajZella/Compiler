package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.DataType;

/*
    <result> = load <ty>, <ty>* <pointer>
    %2 = load i32, i32* @b   ;读取全局变量b
 */
public class Load extends Instr {
    public Load(DataType type, BasicBlock pBB, Value operand) {
        super(ValueType.load, type, pBB, operand);
    }
    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s", name, type, getOperand(0).getType(), getOperand(0).getName());
    }
}
