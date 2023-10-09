package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class RelExp extends AstNode {
    public RelExp() {
        super(GrammarType.RelExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isLAndExp()) { node.checkSema(symbolTable); }
        }
    }
}
