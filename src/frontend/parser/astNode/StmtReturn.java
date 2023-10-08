package frontend.parser.astNode;

public class StmtReturn extends Stmt {
    private Exp exp = null;
    public StmtReturn() {
        super(StmtType.StmtReturn);
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
}
