package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class UnaryOp extends AstNode {
    public UnaryOp() {
        super(GrammarType.UnaryOp);
    }
    public void checkSema(SymbolTable symbolTable) { return; }
}
