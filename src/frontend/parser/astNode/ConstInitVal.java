package frontend.parser.astNode;

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
}
