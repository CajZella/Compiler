package Lexer;

import Parser.GrammarElements.gElement;
import Parser.GrammarType;

public class Token extends gElement {
    private final WordType type;
    private final String value;
    private final int line;
    public Token(WordType type, String value, int line) {
        super(GrammarType.Token);
        this.type = type;
        this.value = value;
        this.line = line;
    }
    public int getLine() { return this.line; }
    public WordType getType() { return this.type; }
    public String getValue() { return this.value; }
    public String toString() { return String.format("%s %s", type, value); }
}
