package Lexer;

public class Token {
    private SysYType type;
    private String value;
    private int line;
    public Token(SysYType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }
    public int getLine() { return this.line; }
    public SysYType getType() { return this.type; }
    public String getValue() { return this.value; }
    public String toString() { return String.format("%s %s", type, value); }
}
