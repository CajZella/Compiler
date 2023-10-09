package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class EqExp extends AstNode {
    public EqExp() {
        super(GrammarType.EqExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isRelExp()) { node.checkSema(symbolTable); }
        }
    }
}
