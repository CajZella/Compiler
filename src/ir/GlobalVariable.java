package ir;

import ir.constants.Constant;
import ir.types.Type;

public class GlobalVariable extends Value {
    private boolean isConstant = false;
    private Constant initializer = null; // 一定有, 维度需要补充完整
    private boolean isString = false;
    private static int stringCnt = 0;
    public GlobalVariable(String name, Type type, boolean isConstant, Constant initializer) {
        super(ValueType.GlobalVariable, String.format("@%s", name), type);
        this.isConstant = isConstant;
        this.initializer = initializer;
    }
    public GlobalVariable(Type type, Constant initializer) {
        super(ValueType.GlobalVariable, String.format("@str.%d", stringCnt++), type);
        this.isConstant = true;
        this.isString = true;
        this.initializer = initializer;
    }
    public boolean isString() { return this.isString; }
    public boolean isConstant() { return isConstant; }
    public boolean hasInitializer() { return null != this.initializer; }
    public Constant getInitializer() { return this.initializer; }
    @Override
    public String toString() {
        if (isString)
            return String.format("%s = private unnamed_addr constant %s %s", this.name, this.initializer.getType(), this.initializer);
        else
            return String.format("%s = dso_local %s %s %s", this.name, isConstant ? "constant" : "global", this.initializer.getType(), this.initializer);
    }
}
