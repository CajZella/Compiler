package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.symbolTable.SymbolTable;

public class BType extends AstNode {
    public BType() {
        super(GrammarType.BType);
    }
    public Token getBType() {
        return (Token)elements.get(0);
    }
    public void checkSema(SymbolTable symbolTable) { return; }
}
