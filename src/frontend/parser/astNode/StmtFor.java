package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class StmtFor extends Stmt {
    private ForStmt forStmt1 = null;
    private Cond cond = null;
    private  ForStmt forStmt2 = null;
    private Stmt stmt;
    public StmtFor() {
        super(StmtType.StmtFor);
    }
    public void setForStmt1(ForStmt forStmt1) {
        this.forStmt1 = forStmt1;
    }
    public void setCond(Cond cond) {
        this.cond = cond;
    }
    public void setForStmt2(ForStmt forStmt2) {
        this.forStmt2 = forStmt2;
    }
    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }
    public boolean hasForStmt1() {
        return forStmt1 != null;
    }
    public ForStmt getForStmt1() {
        return forStmt1;
    }
    public boolean hasCond() {
        return cond != null;
    }
    public Cond getCond() {
        return cond;
    }
    public boolean hasForStmt2() {
        return forStmt2 != null;
    }
    public ForStmt getForStmt2() {
        return forStmt2;
    }
    public Stmt getStmt() {
        return stmt;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (hasForStmt1()) {
            forStmt1.checkSema(symbolTable);
        }
        if (hasCond()) {
            cond.checkSema(symbolTable);
        }
        if (hasForStmt2()) {
            forStmt2.checkSema(symbolTable);
        }
        stmt.setFuncType(funcType);
        stmt.setInLoop(true);
        stmt.checkSema(symbolTable);
    }
}
