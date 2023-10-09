package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class LOrExp extends AstNode {
    public LOrExp() {
        super(GrammarType.LOrExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isLAndExp()) { node.checkSema(symbolTable); }
        }
    }
}
