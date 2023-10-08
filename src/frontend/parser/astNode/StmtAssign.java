package frontend.parser.astNode;

public class StmtAssign extends Stmt {
    private LVal lVal;
    private Exp exp;
    public StmtAssign() {
        super(StmtType.StmtAssign);
    }
    public void setLVal(LVal lVal) {
        this.lVal = lVal;
    }
    public void setExp(Exp exp) {
        this.exp = exp;
    }
    public LVal getLVal() {
        return lVal;
    }
    public Exp getExp() {
        return exp;
    }
}
