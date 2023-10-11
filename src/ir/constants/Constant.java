package ir.constants;

import ir.User;
import ir.types.Type;

// 常量
public abstract class Constant extends User {
    public Constant(ValueType valueTy, Type type) {
        super(valueTy, null, type);
    }
    @Override
    public abstract String toString();
}
