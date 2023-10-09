package frontend.lexer;

import frontend.parser.astNode.AstNode;
import frontend.parser.astNode.GrammarType;
import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.SymbolTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Token extends AstNode {
    private final WordType type;
    private final String value;
    private final int line;
    public Token(WordType type, String value, int line) {
        super(GrammarType.Token);
        this.type = type;
        this.value = value;
        this.line = line;
        errorCheck();
    }
    public int getLine() { return this.line; }
    public WordType getType() { return this.type; }
    public String getValue() { return this.value; }
    public void errorCheck() {
        if (type == WordType.STRCON) {
            String val = value.substring(1, value.length() - 1);
            String regrex = "[\\x00-\\x1F\\x22-\\x24\\x26\\x27\\x7F]";
            Pattern pattern = Pattern.compile(regrex);
            Matcher matcher = pattern.matcher(val);
            if (matcher.find()) {
                ErrorLog.addError(ErrorType.INVALID_CHAR_IN_FSTRING, line);
            }
            if (val.matches(".*?\\\\([^n]|$).*") || val.matches(".*?%([^d]|$).*")) {
                ErrorLog.addError(ErrorType.INVALID_CHAR_IN_FSTRING, line);
            }
        }
    }
    public void checkSema(SymbolTable symbolTable) { return; }
    public String toString() { return String.format("%s %s", type, value); }
}
