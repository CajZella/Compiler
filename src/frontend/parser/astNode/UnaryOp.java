package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.symbolTable.SymbolTable;

public class UnaryOp extends AstNode {
    public UnaryOp() {
        super(GrammarType.UnaryOp);
    }
    public Token getUnaryOp() { return (Token)elements.get(0); }
    public void checkSema(SymbolTable symbolTable) { return; }
}
