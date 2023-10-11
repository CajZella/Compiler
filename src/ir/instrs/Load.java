package ir.instrs;

import ir.BasicBlock;
import ir.types.DataType;

/*
    <result> = load <ty>, <ty>* <pointer>
    %2 = load i32, i32* @b   ;读取全局变量b
 */
public class Load extends Instr {
    public Load(int num, DataType type, BasicBlock pBB) {
        super(ValueType.load, String.format("%%d", num), type, pBB);
    }
    @Override
    public String toString() {
        return String.format("%s = load %s, %s* %s", name, type, getOperand(0).getType(), getOperand(0).getName());
    }
}
