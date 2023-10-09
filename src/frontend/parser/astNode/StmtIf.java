package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class StmtIf extends Stmt {
    private Cond cond = null;
    private Stmt stmtIf = null;
    private Stmt stmtElse = null;
    public StmtIf() {
        super(StmtType.StmtIf);
    }
    public void setCond(Cond cond) {
        this.cond = cond;
    }
    public void setStmtIf(Stmt stmtIf) {
        this.stmtIf = stmtIf;
    }
    public void setStmtElse(Stmt stmtElse) {
        this.stmtElse = stmtElse;
    }
    public Cond getCond() {
        return cond;
    }
    public Stmt getStmtIf() {
        return stmtIf;
    }
    public boolean hasElse() {
        return stmtElse != null;
    }
    public Stmt getStmtElse() {
        return stmtElse;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (cond != null) {
            cond.checkSema(symbolTable);
        }
        if (stmtIf != null) {
            stmtIf.setFuncType(funcType);
            stmtIf.setInLoop(isInLoop);
            stmtIf.checkSema(symbolTable);
        }
        if (hasElse()) {
            stmtElse.setFuncType(funcType);
            stmtElse.setInLoop(isInLoop);
            stmtElse.checkSema(symbolTable);
        }
    }
}
