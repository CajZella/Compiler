package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class MulExp extends AstNode {
    public MulExp() {
        super(GrammarType.MulExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isUnaryExp()) { node.checkSema(symbolTable); }
        }
    }
}
