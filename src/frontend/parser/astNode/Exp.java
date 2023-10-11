package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class Exp extends AstNode {
    public Exp() {
        super(GrammarType.Exp);
    }
    public void checkSema(SymbolTable symbolTable) {
        getAddExp().checkSema(symbolTable);
    }
    public AddExp getAddExp() { return (AddExp)elements.get(0); }
    public int getOpResult() { return getAddExp().getOpResult(); }
}
