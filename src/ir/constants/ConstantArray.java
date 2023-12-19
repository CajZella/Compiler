package ir.constants;

import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.Type;
import util.MyLinkedList;

import java.util.ArrayList;
import java.util.Iterator;

/*
    constant array
    @a = dso_local global [10 x [20 x i32]] zeroinitializer
    @b = dso_local constant [6 x i32] [i32 3, i32 1, i32 8, i32 0, i32 0, i32 0]
    @c = dso_local constant <{ <{ [10 x i32], [90 x i32] }>, <{ i32, i32, [98 x i32] }>, [98 x [100 x i32]] }> <{ <{ [10 x i32], [90 x i32] }> <{ [10 x i32] [i32 1, i32 2, i32 3, i32 4, i32 5, i32 6, i32 7, i32 8, i32 9, i32 10], [90 x i32] zeroinitializer }>, <{ i32, i32, [98 x i32] }> <{ i32 3, i32 4, [98 x i32] zeroinitializer }>, [98 x [100 x i32]] zeroinitializer }>
    不必考虑c这种情况，因为InitVal若有，则必须是于多维数组中数组维数和各维长度完全对应的初始值
 */
public class ConstantArray extends Constant {
    private MyLinkedList<Constant> vals;
    public ConstantArray(Type type) {
        super(ValueType.ConstantArray, null, type);
        this.vals = new MyLinkedList<>();
    }
    public ConstantArray(Type type, boolean non) {
        super(ValueType.ConstantArray, null, type);
        this.vals = ((ConstantArray) initial(type)).getVals();
    }
    public Constant initial(Type type) {
        if (type instanceof IntegerType)
            return new ConstantInt(new IntegerType(32), 0);
        ConstantArray constantArray = new ConstantArray(type);
        for (int i = 0; i < ((ArrayType)type).getLength(); i++)
            constantArray.addVal(initial(((ArrayType)type).getElementType()));
        return constantArray;
    }
    public boolean isEmpty() {return this.vals.isEmpty(); }
    public Constant get(int idx) { return this.vals.get(idx); }
    public void addVal(Constant val) { this.vals.insertAtTail(val); }
    public Constant getElement(int... idxs) {
        Constant constant = this;
        for (int idx : idxs) {
            constant = ((ConstantArray)constant).get(idx);
        }
        return constant;
    }
    public Constant getElement(ArrayList<Integer> idxs, Type type) {
        if (this.type.equals(type))
            idxs.remove(0);
        Constant constant = this;
        for (Integer idx : idxs) {
            constant = ((ConstantArray)constant).get(idx);
        }
        return constant;
    }
    public ArrayList<Integer> getBases() {
        ArrayList<Integer> bases = new ArrayList<>();
        for (Constant constant : vals)
            if (constant instanceof ConstantInt)
                bases.add(((ConstantInt)constant).getVal());
            else
                bases.addAll(((ConstantArray)constant).getBases());
        return bases;
    }
    public MyLinkedList<Constant> getVals() { return this.vals; }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (vals.isEmpty())
            builder.append("zeroinitializer");
        else {
            builder.append("[");
            for (int i = 0; i < vals.size(); i++) {
                Constant constant = vals.get(i);
                builder.append(String.format("%s %s", constant.getType(), constant));
                if (i != vals.size() - 1)
                    builder.append(", ");
            }
            builder.append("]");
        }
        return builder.toString();
    }
}
