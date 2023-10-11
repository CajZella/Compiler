package frontend.symbolTable;

import ir.types.Type;

public class Symbol {
    private final String ident;
    private final boolean isConst;
    private final boolean isGlobal;
    private final Type type;
    private final int line;
    private Initializer initializer = null;

    public Symbol(String ident, boolean isConst, boolean isGlobal, Type type,  int line) {
        this.ident = ident;
        this.isConst = isConst;
        this.type = type;
        this.line = line;
        this.isGlobal = isGlobal;
    }
    public void setInitializer(Initializer initializer) { this.initializer = initializer; }
    public String getIdent() { return this.ident; }
    public boolean isConst() { return this.isConst; }
    public Type getType() { return this.type; }
    public int getLine() { return this.line; }
    public Initializer getInitializer() { return this.initializer; }
}
