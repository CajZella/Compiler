package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.lexer.WordType;

public class StmtBrkCon extends Stmt {
    private Token token;
    public StmtBrkCon(Token token) {
        super(token.getType() == WordType.BREAKTK ? StmtType.StmtBreak : StmtType.StmtContinue);
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
}
