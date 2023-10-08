package settings;

public class Config {
    public static final String sourceFile = "src/testfile.txt"; // todo: 提交或打包时删去src
    public static final String targetFile = "src/output.txt"; // todo: 提交或打包时删去src
    public static final Boolean isLexerOutput = true; // TokenManager的getNextToken()

    public static final Boolean isParserOutput = true;
}
