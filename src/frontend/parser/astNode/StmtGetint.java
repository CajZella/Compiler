package frontend.parser.astNode;

public class StmtGetint extends Stmt {
    private LVal lVal = null;
    public StmtGetint() {
        super(StmtType.StmtGetint);
    }
    public void setLVal(LVal lVal) {
        this.lVal = lVal;
    }
    public LVal getLVal() {
        return lVal;
    }
}
