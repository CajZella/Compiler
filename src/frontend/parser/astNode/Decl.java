package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class Decl extends AstNode {
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;
    public Decl() {
        super(GrammarType.Decl);
    }
    public void setConstDecl(ConstDecl constDecl) {
        this.constDecl = constDecl;
    }
    public void setVarDecl(VarDecl varDecl) {
        this.varDecl = varDecl;
    }
    public boolean isConstDecl() { return constDecl != null; }
    public ConstDecl getConstDecl() { return constDecl; }
    public VarDecl getVarDecl() { return varDecl; }
}