package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class Cond extends AstNode {
//    private LOrExp lOrExp;
    public Cond() {
        super(GrammarType.Cond);
    }
    public LOrExp getlOrExp() { return (LOrExp) elements.get(0); }
//    public void setlOrExp(LOrExp lOrExp) { this.lOrExp = lOrExp; }
//    public LOrExp getlOrExp() { return lOrExp; }
    public void checkSema(SymbolTable symbolTable) {
        getlOrExp().checkSema(symbolTable);
    }
}
