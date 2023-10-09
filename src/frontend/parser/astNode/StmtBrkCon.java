package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.lexer.WordType;
import frontend.symbolTable.SymbolTable;

public class StmtBrkCon extends Stmt {
    private Token token;
    public StmtBrkCon(Token token) {
        super(token.getType() == WordType.BREAKTK ? StmtType.StmtBreak : StmtType.StmtContinue);
        this.token = token;
    }
    public Token getToken() {
        return token;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (!isInLoop) {
            ErrorLog.addError(ErrorType.BREAK_CONTINUE_MISPLACED, token.getLine());
        }
    }
}
