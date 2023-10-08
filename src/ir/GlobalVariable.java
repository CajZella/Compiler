package ir;

import ir.constants.Constant;
import ir.constants.GlobalValue;
import ir.types.Type;

public class GlobalVariable extends GlobalValue {
    private Type type;
    private boolean isConstant;
    private Constant initializer = null;
    private String name;

    public boolean isConstant() { return this.isConstant; }
    public boolean hasInitializer() { return null != this.initializer; }
    public Constant getInitializer() { return this.initializer; }
}
