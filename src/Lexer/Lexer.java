package Lexer;

import util.MyIO;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private int curLine; // 当前所在行
    private String curContent; // 当前行内容
    private int curPos; // 当前字符位置

    public Lexer() {
        this.curLine = 0;
        nextLine();
        this.curPos = 0;
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

    public boolean hasNextToken() {
        if (curContent == null) return false;
        boolean flag = true;
        while (true) {
            if (curPos >= curContent.length()) {
                if (!nextLine()) {flag = false; break;}
            }
            // delete meaningless character or comment
            else if (blankMatcher.find(curPos) && blankMatcher.start() == curPos) {
                if (ignoreBlank()) {flag = false; break;}
            }
            else { break; }
        }
        return flag;
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
    public Token nextToken() {
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
            return !nextLine();
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
        return switch (word) {
            case "==" -> new Token(WordType.EQL, "==", curLine);
            case "!=" -> new Token(WordType.NEQ, "!=", curLine);
            case "<=" -> new Token(WordType.LEQ, "<=", curLine);
            case ">=" -> new Token(WordType.GEQ, ">=", curLine);
            case "&&" -> new Token(WordType.AND, "&&", curLine);
            case "||" -> new Token(WordType.OR, "||", curLine);
            default -> null;
        };
    }
    private Token singleDelimiter(String word) {
        curPos++;
        return switch (word) {
            case "+" -> new Token(WordType.PLUS, "+", curLine);
            case "-" -> new Token(WordType.MINU, "-", curLine);
            case "*" -> new Token(WordType.MULT, "*", curLine);
            case "/" -> new Token(WordType.DIV, "/", curLine);
            case "%" -> new Token(WordType.MOD, "%", curLine);
            case "<" -> new Token(WordType.LSS, "<", curLine);
            case ">" -> new Token(WordType.GRE, ">", curLine);
            case "=" -> new Token(WordType.ASSIGN, "=", curLine);
            case "!" -> new Token(WordType.NOT, "!", curLine);
            case "," -> new Token(WordType.COMMA, ",", curLine);
            case ";" -> new Token(WordType.SEMICN, ";", curLine);
            case "(" -> new Token(WordType.LPARENT, "(", curLine);
            case ")" -> new Token(WordType.RPARENT, ")", curLine);
            case "[" -> new Token(WordType.LBRACK, "[", curLine);
            case "]" -> new Token(WordType.RBRACK, "]", curLine);
            case "{" -> new Token(WordType.LBRACE, "{", curLine);
            case "}" -> new Token(WordType.RBRACE, "}", curLine);
            default -> null;
        };
    }
    private Token unsignedInteger(String word) {
        curPos += word.length();
        return new Token(WordType.INTCON, word, curLine);
    }
    private Token identifier(String word) {
        curPos += word.length();
        return switch (word) {
            case "main" -> new Token(WordType.MAINTK, "main", curLine);
            case "const" -> new Token(WordType.CONSTTK, "const", curLine);
            case "int" -> new Token(WordType.INTTK, "int", curLine);
            case "break" -> new Token(WordType.BREAKTK, "break", curLine);
            case "continue" -> new Token(WordType.CONTINUETK, "continue", curLine);
            case "if" -> new Token(WordType.IFTK, "if", curLine);
            case "else" -> new Token(WordType.ELSETK, "else", curLine);
            case "while" -> new Token(WordType.WHILETK, "while", curLine);
            case "for" -> new Token(WordType.FORTK, "for", curLine);
            case "getint" -> new Token(WordType.GETINTTK, "getint", curLine);
            case "printf" -> new Token(WordType.PRINTFTK, "printf", curLine);
            case "return" -> new Token(WordType.RETURNTK, "return", curLine);
            case "void" -> new Token(WordType.VOIDTK, "void", curLine);
            default -> new Token(WordType.IDENFR, word, curLine);
        };
    }
    private Token stringConst(String word) {
        curPos += word.length();
        return new Token(WordType.STRCON, word, curLine);
    }
    private void handleError(String err) {
        // todo
        System.err.println("Error: "+ curLine + ": " + err);
        System.exit(-1);
    }
}
