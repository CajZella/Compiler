package ir;

import ir.types.DataType;
import ir.types.Type;

/*
 * 函数形参
 * eg. define dso_local void @mp(i32 %0, i32* %1, [2 x i32]* %2)
 * dataType %tot
*/
public class Argument extends Value {
    public Argument(DataType dataType) {
        super(ValueType.Argument, "%ar" + num++, dataType);
    }
    @Override
    public String toString() {
        return String.format("%s %s", type, name);
    }
}
