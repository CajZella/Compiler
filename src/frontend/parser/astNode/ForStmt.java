package frontend.parser.astNode;

public class ForStmt extends AstNode {
    private LVal lVal;
    private Exp exp;
    public ForStmt() {
        super(GrammarType.ForStmt);
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
