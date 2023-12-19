package frontend.lexer;

public enum WordType {
    IDENFR(null),
    INTCON(null),
    STRCON(null),
    MAINTK("main"),
    CONSTTK("const"),
    INTTK("int"),
    BREAKTK("break"),
    CONTINUETK("continue"),
    IFTK("if"),
    ELSETK("else"),
    FORTK("for"),
    RETURNTK("return"),
    VOIDTK("void"),
    WHILETK("while"),
    GETINTTK("getint"),
    PRINTFTK("printf"),

    NOT("!"),
    AND("&&"),
    OR("||"),
    PLUS("+"),
    MINU("-"),
    MULT("*"),
    DIV("/"),
    MOD("%"),
    LSS("<"),
    LEQ("<="),
    GRE(">"),
    GEQ(">="),
    EQL("=="),
    NEQ("!="),
    ASSIGN("="),
    LPARENT("("),
    RPARENT(")"),
    LBRACK("["),
    RBRACK("]"),
    LBRACE("{"),
    RBRACE("}"),
    SEMICN(";"),
    COMMA(","),
    ;
    private final String val;
    WordType(final String val) { this.val = val; }
    public String getVal() { return this.val; }
    public static String getVal(WordType type) { return type.getVal(); }
}
