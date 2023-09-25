package Parser;

import Lexer.Lexer;
import Lexer.Token;
import Lexer.WordType;
import Settings.Configure;
import java.util.LinkedList;

/**
 * 调用Lexer接口，获取下一个token
 * 提供Parser接口，获取第x个token，判断token类型(超前扫描)
 */
public class TokenManager {
    private final Lexer lexer;
    private final LinkedList<Token> buffer;

    public TokenManager() {
        this.lexer = new Lexer();
        this.buffer = new LinkedList<>();
    }
    public Token getNextToken() {
        if (buffer.isEmpty()) {
            if (lexer.hasNextToken()) {
                buffer.add(lexer.nextToken());
            }
        }
        Token token = buffer.removeFirst();
        Configure.lexDisplay(token.toString());
        return token;
    }
    public boolean checkTokenType(int index, WordType... wtype) {
        while(buffer.size() <= index) {
            if (lexer.hasNextToken()) {
                buffer.add(lexer.nextToken());
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
}
