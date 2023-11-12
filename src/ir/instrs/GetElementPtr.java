package ir.instrs;

import ir.BasicBlock;
import ir.Value;
import ir.types.PointerType;

import java.util.ArrayList;

/*
    calculate address
    <result> = getelemeterptr <ty>, <ty>* <ptrval>, {<ty> <index>}*
    第一个<ty>表示指针所指向的类型
    第二个<ty>表示后面的指针基址<ptrval>的类型
    <ty> <index> 表示一组索引的类型和值，其中第一个索引不会改变返回的指针的类型，
        其偏移量由索引的值和指针指向的类型共同决定，接下来每增加一个索引，就会使该
        索引使用的基本类型和返回的指针的类型指向原类型的元素。
    <result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*
    <result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}* 索引指针类型
    具体场景：
    1. 一维数组
        %array = alloca [10 x i32]
        %ptr = getelementptr [10 x i32], [10 x i32]* %array, i32 0, i32 1 ;指向array[1]的指针
    2. 二维数组
        %array = alloca [5 x [5 x i32]]
        %ptr = getelementptr [5 x [5 x i32]], [5 x [5 x i32]]* %array, i32 0, i32 1  ;指向array[1]的指针
        %ptr = getelementptr [5 x [5 x i32]], [5 x [5 x i32]]* %array, i32 0, i32 1, i32 2 ;指向array[1][2]的指针
 */
public class GetElementPtr extends Instr {
    public GetElementPtr(PointerType type, BasicBlock pBB, Value... operands) {
        super(ValueType.getelementptr, type, pBB, operands);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s = getelementptr %s, %s %s", name,
                ((PointerType)getOperand(0).getType()).getReferencedType(),
                getOperand(0).getType(), getOperand(0).getName()));
        for (int i = 1; i < operandsSize(); i++) {
            builder.append(String.format(", i32 %s", getOperand(i).getName()));
        }
        return builder.toString();
    }
}
