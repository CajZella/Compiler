package frontend.parser.astNode;

import frontend.symbolTable.Initializer;
import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

public class ConstInitVal extends AstNode {
    private ConstExp constExp = null;
    private ArrayList<ConstInitVal> constInitVals;
    public ConstInitVal() {
        super(GrammarType.ConstInitVal);
        constInitVals = new ArrayList<>();
    }
    public void setConstExp(ConstExp constExp) { this.constExp = constExp; }
    public void addConstInitVal(ConstInitVal constInitVal) { constInitVals.add(constInitVal); }
    public boolean isConstExp() { return constExp != null; }
    public ConstExp getConstExp() { return constExp; }
    public ArrayList<ConstInitVal> getConstInitVals() { return constInitVals; }
    public void checkSema(SymbolTable symbolTable) {
        if (isConstExp()) {
            constExp.checkSema(symbolTable);
        } else {
            for (ConstInitVal constInitVal : constInitVals) {
                constInitVal.checkSema(symbolTable);
            }
        }
    }
    public Initializer getInit() {
        Initializer init;
        if (isConstExp()) {
            init = new Initializer.IntInitializer(constExp.getOpResult());
        } else {
            Initializer.ArrayInitializer tmp = new Initializer.ArrayInitializer();
            for (ConstInitVal constInitVal : constInitVals) {
                tmp.addInit(constInitVal.getInit());
            }
            init = tmp;
        }
        return init;
    }
}
