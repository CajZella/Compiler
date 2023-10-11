package ir.constants;

import ir.types.Type;

import java.util.ArrayList;

/*
    constant array
    @a = dso_local global [10 x [20 x i32]] zeroinitializer
    @b = dso_local constant [6 x i32] [i32 3, i32 1, i32 8, i32 0, i32 0, i32 0]
    @c = dso_local constant <{ <{ [10 x i32], [90 x i32] }>, <{ i32, i32, [98 x i32] }>, [98 x [100 x i32]] }> <{ <{ [10 x i32], [90 x i32] }> <{ [10 x i32] [i32 1, i32 2, i32 3, i32 4, i32 5, i32 6, i32 7, i32 8, i32 9, i32 10], [90 x i32] zeroinitializer }>, <{ i32, i32, [98 x i32] }> <{ i32 3, i32 4, [98 x i32] zeroinitializer }>, [98 x [100 x i32]] zeroinitializer }>
    不必考虑c这种情况，因为InitVal若有，则必须是于多维数组中数组维数和各维长度完全对应的初始值
 */
public class ConstantArray extends Constant {
    private ArrayList<Constant> vals;
    public ConstantArray(Type type, ArrayList<Constant> vals) {
        super(ValueType.ConstantArray, type);
        this.vals = vals;
    }
    public ArrayList<Constant> getVals() { return this.vals; }
    public int getVal(ArrayList<Integer> idxs) {
        Constant constant = this;
        for (Integer idx : idxs) {
            constant = ((ConstantArray)constant).getVals().get(idx);
        }
        return ((ConstantInt)constant).getVal();
    }
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
