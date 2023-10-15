package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;
import ir.Value;

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
    public void checkSema(SymbolTable symbolTable) {
        if (isConstDecl()) {
            constDecl.setGlobal(isGlobal);
            constDecl.checkSema(symbolTable);
        } else {
            varDecl.setGlobal(isGlobal);
            varDecl.checkSema(symbolTable);
        }
    }
}
