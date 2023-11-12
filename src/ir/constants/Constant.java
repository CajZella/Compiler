package ir.constants;

import ir.User;
import ir.Value;
import ir.types.Type;

// 常量
public abstract class Constant extends Value {
    public Constant(ValueType valueTy, String name, Type type) {
        super(valueTy, name, type);
    }
    @Override
    public abstract String toString();
}
