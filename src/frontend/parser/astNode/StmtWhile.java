package frontend.parser.astNode;

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

}
