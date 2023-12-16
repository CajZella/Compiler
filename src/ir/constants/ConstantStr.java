package ir.constants;

import ir.types.Type;

import java.util.ArrayList;

public class ConstantStr extends Constant {
    private String val;
    public ConstantStr(String val, Type type) {
        super(ValueType.ConstantStr, null, type);
        this.val = val;
    }
    public String getVal() { return this.val; }
    public Constant getElement(ArrayList<Integer> idxs, Type type) { return null; }
    @Override
    public String toString() {
        return String.format("c\"%s\"", val);
    }
}
