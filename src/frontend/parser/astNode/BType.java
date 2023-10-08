package frontend.parser.astNode;

import frontend.lexer.Token;

public class BType extends AstNode {
    public BType() {
        super(GrammarType.BType);
    }
    public Token getBType() {
        return (Token)elements.get(0);
    }
}
