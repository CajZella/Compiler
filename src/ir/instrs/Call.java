package ir.instrs;

import ir.BasicBlock;
import ir.Function;
import ir.Value;
import ir.types.DataType;
import ir.types.IntegerType;

import java.util.ArrayList;

/*
    <result> = call [ret attrs] <ty> <fnptrval>(<function args>)
    %7 = call i32 @aaa(i32 %5, i32 %6)
 */
public class Call extends Instr {
    public Call(DataType type, BasicBlock pBB, Value...operands) {
        super(ValueType.call, type, pBB, operands);
    }
    public Function getCallee() { return (Function) getOperand(0); }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (type.isVoidTy())
            builder.append(String.format("call void %s(", getOperand(0).getName()));
        else
            builder.append(String.format("%s = call %s %s(", getName(), type, getOperand(0).getName()));
        for (int i = 1; i < operandsSize(); i++) {
            Value operand = getOperand(i);
            builder.append(String.format("%s %s", operand.getType(), operand.getName()));
            if (i != operandsSize() - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
