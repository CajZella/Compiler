package frontend.symbolTable;

import frontend.parser.astNode.ConstExp;
import ir.Value;
import ir.constants.Constant;
import ir.types.Type;

public class Symbol {
    private final String ident;
    private final boolean isConst;
    private final boolean isGlobal;
    private final Type type;
    private final int line;
    private Constant constantInit = null;
    private Value irPtr = null;

    public Symbol(String ident, boolean isConst, boolean isGlobal, Type type,  int line) {
        this.ident = ident;
        this.isConst = isConst;
        this.type = type;
        this.line = line;
        this.isGlobal = isGlobal;
    }
    public void setIrPtr(Value irPtr) { this.irPtr = irPtr; }
    public void setConstantInit(Constant init) { this.constantInit = init; }
    public String getIdent() { return this.ident; }
    public boolean isConst() { return this.isConst; }
    public Type getType() { return this.type; }
    public int getLine() { return this.line; }
    public Constant getConstantInit() { return this.constantInit; }
    public boolean isGlobal() { return isGlobal; }
    public Value getIrPtr() { return this.irPtr; }
}
