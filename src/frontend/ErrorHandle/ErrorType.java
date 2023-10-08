package frontend.ErrorHandle;

public enum ErrorType {
    INVALID_CHAR_IN_FSTRING("invalid character in format string", 'a'),
    DUPLICATE_IDENFR("duplicate function or variable defination", 'b'),
    UNDEFINED_IDENFR("undefined function or variable", 'c'),
    FUNC_PARAM_NUMBER_MISMATCHED("mismatched number of function param", 'd'),
    FUNC_PARAM_TYPE_MISMATCHED("mismatched type of function param", 'e'),
    RETURN_VALUE_IN_VOID_FUNC("mismatched type of return value", 'f'),
    RETURN_MISSED("missing return value", 'g'),
    CONST_ASSIGNMENT("assigning value to constant", 'h'),
    SEMICN_MISSED("missing semicolon", 'i'),
    RPARENT_MISSED("missing right parenthesis", 'j'),
    RBRACK_MISSED("missing right bracket", 'k'),
    PRINTF_MISMATCHED("mismatched number of parameters in printf", 'l'),
    BREAK_CONTINUE_MISPLACED("use break or continue in non-loop blocks", 'm'),
    ;

    private final String msg;
    private final char type;
    ErrorType(final String msg, final char type) {
        this.msg = msg;
        this.type = type;
    }
    public String getMsg() {
        return msg;
    }
    public char getType() {
        return type;
    }
}
