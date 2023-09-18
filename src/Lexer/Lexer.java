package Lexer;

import util.MyIO;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    // 当前所在行
    private int curLine;
    // 当前行内容
    private String curContent;
    // 当前字符位置
    private int curPos;
    // 词法分析完成的token数组
    private final ArrayList<Token> tokens;

    public Lexer() {
        this.curLine = 0;
        nextLine();
        this.curPos = 0;
        this.tokens = new ArrayList<>();
    }
    private final String blankRegrex = "\\s|//|/\\*";
    private final Pattern blankPattern = Pattern.compile(blankRegrex);
    private Matcher blankMatcher;
    private final String doubleDelimiterRegrex = "==|!=|<=|>=|&&|\\|\\|";
    private final String singleDelimiterRegrex = "[+\\-*/%<>=!,;(){}\\[\\]]";
    private final String unsignedIntegerRegrex = "[1-9][0-9]*|0";
    private final String identifierRegrex = "[a-zA-Z_][a-zA-Z_0-9]*";
    private final String stringConstRegrex = "\".*\"";
    private final Pattern doubleDelimiterPattern = Pattern.compile(doubleDelimiterRegrex);
    private final Pattern singleDelimiterPattern = Pattern.compile(singleDelimiterRegrex);
    private final Pattern unsignedIntegerPattern = Pattern.compile(unsignedIntegerRegrex);
    private final Pattern identifierPattern = Pattern.compile(identifierRegrex);
    private final Pattern stringConstPattern = Pattern.compile(stringConstRegrex);
    private Matcher doubleDelimiterMatcher;
    private Matcher singleDelimiterMatcher;
    private Matcher unsignedIntegerMatcher;
    private Matcher identifierMatcher;
    private Matcher stringConstMatcher;
    public void run() {
        while (true) {
            if (curLine == 16) {
                int a = 1;
            }
            if (curPos >= curContent.length()) {
                if (!nextLine()) break;
            }
            // delete meaningless character or comment
            else if (blankMatcher.find(curPos) && blankMatcher.start() == curPos) {
                if (ignoreBlank()) break;
            }
            else {
                tokens.add(getToken());
            }

        }
    }
    public ArrayList<Token> getTokens() {
        return tokens;
    }
    private boolean nextLine() {
        this.curContent = MyIO.readLine();
        if (this.curContent == null) { return false; } // 结束
        this.curLine++;
        this.curPos = 0;
        blankMatcher = blankPattern.matcher(curContent);
        doubleDelimiterMatcher = doubleDelimiterPattern.matcher(curContent);
        singleDelimiterMatcher = singleDelimiterPattern.matcher(curContent);
        unsignedIntegerMatcher = unsignedIntegerPattern.matcher(curContent);
        identifierMatcher = identifierPattern.matcher(curContent);
        stringConstMatcher = stringConstPattern.matcher(curContent);
        return true;
    }
    private Token getToken() {
        if (doubleDelimiterMatcher.find(curPos) && doubleDelimiterMatcher.start() == curPos) {
            return doubleDelimiter(doubleDelimiterMatcher.group());
        }
        else if (singleDelimiterMatcher.find(curPos) && singleDelimiterMatcher.start() == curPos) {
            return singleDelimiter(singleDelimiterMatcher.group());
        }
        else if (unsignedIntegerMatcher.find(curPos) && unsignedIntegerMatcher.start() == curPos) {
            return unsignedInteger(unsignedIntegerMatcher.group());
        }
        else if (identifierMatcher.find(curPos) && identifierMatcher.start() == curPos) {
            return identifier(identifierMatcher.group());
        }
        else if (stringConstMatcher.find(curPos) && stringConstMatcher.start() == curPos) {
            return stringConst(stringConstMatcher.group());
        }
        else {
            handleError("Unexpected character.");
            return null;
        }
    }
    private boolean ignoreBlank() {
        if (curContent.substring(curPos).startsWith("//")) {
            if (!nextLine()) { return true; }
        }
        else if (curContent.substring(curPos).startsWith("/*")) {
            curPos += 2;
            while ((curPos=curContent.indexOf("*/", curPos)) == -1) {
                if (!nextLine()) { handleError("Comment is not closed."); }
            }
            curPos += 2;
        }
        else { curPos++; }
        return false;
    }
    private Token doubleDelimiter(String word) {
        curPos += 2;
        switch (word) {
            case "==":
                return new Token(SysYType.EQL, "==", curLine);
            case "!=":
                return new Token(SysYType.NEQ, "!=", curLine);
            case "<=":
                return new Token(SysYType.LEQ, "<=", curLine);
            case ">=":
                return new Token(SysYType.GEQ, ">=", curLine);
            case "&&":
                return new Token(SysYType.AND, "&&", curLine);
            case "||":
                return new Token(SysYType.OR, "||", curLine);
            default:
                return null;
        }
    }
    private Token singleDelimiter(String word) {
        curPos++;
        switch (word) {
            case "+":
                return new Token(SysYType.PLUS, "+", curLine);
            case "-":
                return new Token(SysYType.MINU, "-", curLine);
            case "*":
                return new Token(SysYType.MULT, "*", curLine);
            case "/":
                return new Token(SysYType.DIV, "/", curLine);
            case "%":
                return new Token(SysYType.MOD, "%", curLine);
            case "<":
                return new Token(SysYType.LSS, "<", curLine);
            case ">":
                return new Token(SysYType.GRE, ">", curLine);
            case "=":
                return new Token(SysYType.ASSIGN, "=", curLine);
            case "!":
                return new Token(SysYType.NOT, "!", curLine);
            case ",":
                return new Token(SysYType.COMMA, ",", curLine);
            case ";":
                return new Token(SysYType.SEMICN, ";", curLine);
            case "(":
                return new Token(SysYType.LPARENT, "(", curLine);
            case ")":
                return new Token(SysYType.RPARENT, ")", curLine);
            case "[":
                return new Token(SysYType.LBRACK, "[", curLine);
            case "]":
                return new Token(SysYType.RBRACK, "]", curLine);
            case "{":
                return new Token(SysYType.LBRACE, "{", curLine);
            case "}":
                return new Token(SysYType.RBRACE, "}", curLine);
            default:
                return null;
        }
    }
    private Token unsignedInteger(String word) {
        curPos += word.length();
        return new Token(SysYType.INTCON, word, curLine);
    }
    private Token identifier(String word) {
        curPos += word.length();
        switch (word) {
            case "main":
                return new Token(SysYType.MAINTK, "main", curLine);
            case "const":
                return new Token(SysYType.CONSTTK, "const", curLine);
            case "int":
                return new Token(SysYType.INTTK, "int", curLine);
            case "break":
                return new Token(SysYType.BREAKTK, "break", curLine);
            case "continue":
                return new Token(SysYType.CONTINUETK, "continue", curLine);
            case "if":
                return new Token(SysYType.IFTK, "if", curLine);
            case "else":
                return new Token(SysYType.ELSETK, "else", curLine);
            case "for":
                return new Token(SysYType.FORTK, "for", curLine);
            case "getint":
                return new Token(SysYType.GETINTTK, "getint", curLine);
            case "printf":
                return new Token(SysYType.PRINTFTK, "printf", curLine);
            case "return":
                return new Token(SysYType.RETURNTK, "return", curLine);
            case "void":
                return new Token(SysYType.VOIDTK, "void", curLine);
            default:
                return new Token(SysYType.IDENFR, word, curLine);
        }
    }
    private Token stringConst(String word) {
        curPos += word.length();
        return new Token(SysYType.STRCON, word, curLine);
    }
    private void handleError(String err) {
        // todo
        System.err.println("Error: "+ curLine + ": " + err);
        System.exit(-1);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Token token : tokens) {
            sb.append(token.toString()).append("\n");
        }
        return sb.toString();
    }
}
