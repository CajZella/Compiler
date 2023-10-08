package frontend.parser.astNode;

import java.util.ArrayList;

public class InitVal extends AstNode {
    private Exp exp = null;
    private ArrayList<InitVal> initVals;
    public InitVal() {
        super(GrammarType.InitVal);
        initVals = new ArrayList<>();
    }
    public void addInitVal(InitVal initVal) {
        initVals.add(initVal);
    }
    public void setExp(Exp exp) {
        this.exp = exp;
    }
    public boolean isExp() {
        return exp != null;
    }
    public Exp getExp() {
        return exp;
    }
    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }
}
