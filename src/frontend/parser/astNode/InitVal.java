package frontend.parser.astNode;

import frontend.symbolTable.Initializer;
import frontend.symbolTable.SymbolTable;

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
    public void checkSema(SymbolTable symbolTable) {
        if (isExp()) {
            exp.checkSema(symbolTable);
        } else {
            for (InitVal initVal : initVals) {
                initVal.setGlobal(isGlobal);
                initVal.checkSema(symbolTable);
            }
        }
    }
    public Initializer getInit() {
        Initializer init;
        if (isExp()) {
            if (isGlobal)
                init = new Initializer.IntInitializer(exp.getOpResult());
            else
                init = new Initializer.ExpInitializer(exp);
        } else {
            Initializer.ArrayInitializer tmp = new Initializer.ArrayInitializer();
            for (InitVal initVal : initVals) {
                tmp.addInit(initVal.getInit());
            }
            init = tmp;
        }
        return init;
    }
}
