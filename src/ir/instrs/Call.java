package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.DataType;
import ir.types.IntegerType;

import java.util.ArrayList;

/*
    <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
    %7 = call i32 @aaa(i32 %5, i32 %6)
 */
public class Call extends Instr {
    public Call(int num, DataType type, BasicBlock pBB) {
        super(ValueType.call, type.isVoidTy() ? null : String.format("%%d", num), type, pBB);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ArrayList<Value> operands1 = getOperands();
        if (type.isVoidTy())
            builder.append(String.format("call void %s(", operands1.get(0).getName()));
        else
            builder.append(String.format("%s = call %s %s(", getName(), type, operands1.get(0).getName()));
        for (int i = 1; i < operands1.size(); i++) {
            Value operand = operands1.get(i);
            builder.append(String.format("%s %s", operand.getType(), operand.getName()));
            if (i != operands1.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
