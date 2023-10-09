package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class StmtWhile extends Stmt {
    private Cond cond;
    private Stmt stmt;
    public StmtWhile() {
        super(StmtType.StmtWhile);
    }
    public void setCond(Cond cond) {
        this.cond = cond;
    }
    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }
    public Cond getCond() {
        return cond;
    }
    public Stmt getStmt() {
        return stmt;
    }
    public void checkSema(SymbolTable symbolTable) {
        cond.checkSema(symbolTable);
        stmt.setFuncType(funcType);
        stmt.setInLoop(true);
        stmt.checkSema(symbolTable);
    }
}
