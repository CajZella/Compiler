package ir.constants;

import ir.types.Type;

public class ConstantStr extends Constant {
    private String val;
    public ConstantStr(String val, Type type) {
        super(ValueType.ConstantStr, null, type);
        this.val = val;
    }
    public String toMipsString() {
        String str = val.replaceAll("\\\\0A", "\\\\n");
        str = "\"" + str.replaceAll("\\\\00", "") + "\"";
        return str;
    }
    public String getVal() { return this.val; }
    @Override
    public String toString() {
        return String.format("c\"%s\"", val);
    }
}
