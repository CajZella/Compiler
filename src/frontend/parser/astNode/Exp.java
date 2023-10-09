package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class Exp extends AstNode {
    public Exp() {
        super(GrammarType.Exp);
    }
    public void checkSema(SymbolTable symbolTable) {
        ((AddExp)elements.get(0)).checkSema(symbolTable);
    }
}
