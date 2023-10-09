package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class StmtExp extends Stmt {
    private Exp exp;
    public StmtExp() {
        super(StmtType.StmtExp);
    }
    public void setExp(Exp exp) {
        this.exp = exp;
    }
    public boolean hasExp() {
        return exp != null;
    }
    public Exp getExp() {
        return exp;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (hasExp()) {
            exp.checkSema(symbolTable);
        }
    }
}
