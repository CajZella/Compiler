package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class LAndExp extends AstNode {
    public LAndExp() {
        super(GrammarType.LAndExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isEqExp()) { node.checkSema(symbolTable); }
        }
    }
}
