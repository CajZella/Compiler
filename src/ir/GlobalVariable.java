package ir;

import ir.constants.Constant;
import ir.types.Type;

public class GlobalVariable extends Value {
    private boolean isConstant = false;
    private Constant initializer = null; // 一定有, 维度需要补充完整
    public GlobalVariable(String name, Type type, boolean isConstant, Constant initializer) {
        super(ValueType.GlobalVariable, String.format("@%s", name), type);
        this.isConstant = isConstant;
        this.initializer = initializer;
    }
    public boolean isConstant() { return isConstant; }
    public boolean hasInitializer() { return null != this.initializer; }
    public Constant getInitializer() { return this.initializer; }
    @Override
    public String toString() {
        return String.format("@%s = dso_local %s %s %s", this.name, isConstant ? "constant" : "global", this.type, this.initializer);
    }
}
