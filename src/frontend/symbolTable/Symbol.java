package frontend.symbolTable;

import ir.types.Type;

public class Symbol {
    private final String ident;
    private final boolean isConst;
    private final Type type;
    private Initial initial;
    private final int line;

    public Symbol(String ident, boolean isConst, Type type,  int line) {
        this.ident = ident;
        this.isConst = isConst;
        this.type = type;
        this.line = line;
    }
    public void setInitial(Initial initial) { this.initial = initial; } // 暂时先留到生成中间代码阶段处理
    public String getIdent() { return this.ident; }
    public boolean isConst() { return this.isConst; }
    public Type getType() { return this.type; }
    public Initial getInitial() { return this.initial; }
    public int getLine() { return this.line; }
}
