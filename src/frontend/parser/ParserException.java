package frontend.parser;

public class ParserException extends Exception {
    public enum ParserExcType {
        MISS_SEMICN,
        MISS_RPARENT,
        MISS_RBRACK,
        OTHER,
    }
    public final ParserExcType type;
    public ParserException(ParserExcType type) {
        this.type = type;
    }
    public ParserExcType getType() {
        return this.type;
    }
}
