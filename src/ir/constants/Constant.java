package ir.constants;

import ir.User;
import ir.Value;
import ir.types.Type;

import java.util.ArrayList;

// 常量
public abstract class Constant extends Value {
    public Constant(ValueType valueTy, String name, Type type) {
        super(valueTy, name, type);
    }
    public abstract Constant getElement(ArrayList<Integer> idxs, Type type);
    @Override
    public abstract String toString();
}
