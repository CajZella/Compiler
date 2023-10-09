package frontend.parser;

import frontend.ErrorHandle.ErrorLog;
import frontend.LexParseLog;
import frontend.lexer.Lexer;
import frontend.lexer.Token;
import frontend.lexer.WordType;
import frontend.parser.astNode.AstNode;
import frontend.symbolTable.SymbolTable;
import settings.Configure;
import java.util.LinkedList;

/**
 * 调用Lexer接口，获取下一个token
 * 提供Parser接口，获取第x个token，判断token类型(超前扫描)
 */
public class TokenManager {
    private final Lexer lexer;
    private final LinkedList<Token> buffer;
    // 备份缓冲区，用于回溯
    private boolean isBackup;
    private Token curToken;
    private Token prevToken;
    private final LinkedList<Token> backupBuffer;
    private Token backupCurToken;
    private Token backupPrevToken;
    private int backupErrorLogSize;
    private int backupLexParseLogSize;

    public TokenManager() {
        this.lexer = new Lexer();
        this.buffer = new LinkedList<>();
        this.curToken = null;
        this.isBackup = true;
        this.backupBuffer = new LinkedList<>();
        this.backupCurToken = null;
        this.backupErrorLogSize = -1;
        this.backupLexParseLogSize = -1;
    }
    public Token getNextToken() {
        if (buffer.isEmpty()) {
            if (lexer.hasNextToken()) {
                Token token = lexer.nextToken();
                if (isBackup) { backupBuffer.add(token); }
                buffer.add(token);
            }
        }
        prevToken = curToken;
        curToken = buffer.removeFirst();
        LexParseLog.add(curToken.toString());
        return curToken;
    }
    public Token getNextToken(WordType... wordType) throws ParserException {
        if (buffer.isEmpty()) {
            if (lexer.hasNextToken()) {
                Token token = lexer.nextToken();
                if (isBackup) { backupBuffer.add(token); }
                buffer.add(token);
            }
        }
        prevToken = curToken;
        curToken = buffer.getFirst();
        boolean isMatch = false;
        for (WordType type : wordType) {
            if (curToken.getType() == type) {
                isMatch = true;
                break;
            }
        }
        if (!isMatch) {
            if (wordType.length == 1 && wordType[0] == WordType.SEMICN) {
                throw  new ParserException(ParserException.ParserExcType.MISS_SEMICN);
            } else if (wordType.length == 1 && wordType[0] == WordType.RPARENT) {
                throw  new ParserException(ParserException.ParserExcType.MISS_RPARENT);
            } else if (wordType.length == 1 && wordType[0] == WordType.RBRACK) {
                throw  new ParserException(ParserException.ParserExcType.MISS_RBRACK);
            } else {
                throw new ParserException(ParserException.ParserExcType.OTHER);
            }
        }
        buffer.removeFirst();
        LexParseLog.add(curToken.toString());
        return curToken;
    }
    public Token makeupToken(WordType wordType) {
        return new Token(wordType, wordType.getVal(), prevToken.getLine());
    }

    public boolean checkTokenType(int index, WordType... wtype) {
        while(buffer.size() <= index) {
            if (lexer.hasNextToken()) {
                Token token = lexer.nextToken();
                if (isBackup) { backupBuffer.add(token); }
                buffer.add(token);
            } else {
                return false;
            }
        }
        for(WordType type : wtype) {
            if(buffer.get(index).getType() == type) {
                return true;
            }
        }
        return false;
    }
    public void openBackup() {
        isBackup = true;
        backupBuffer.clear();
        backupBuffer.addAll(buffer);
        backupCurToken = curToken;
        backupPrevToken = prevToken;
        backupErrorLogSize = ErrorLog.size();
        backupLexParseLogSize = LexParseLog.size();
    }
    public void closeBackup() {
        isBackup = false;
        backupBuffer.clear();
    }
    public void rollBack() {
        buffer.clear();
        buffer.addAll(backupBuffer);
        curToken = backupCurToken;
        prevToken = backupPrevToken;
        ErrorLog.remain(backupErrorLogSize);
        LexParseLog.remain(backupLexParseLogSize);
        isBackup = false;
        backupBuffer.clear();
    }
}
