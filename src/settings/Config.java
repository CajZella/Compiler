package settings;

public class Config {
    public static String sourceFile = "src/testfile.txt"; // todo: 提交或打包时删去src
    public static String targetFile = "src/output.txt"; // todo: 提交或打包时删去src
    public static String errorFile = "src/error.txt"; // todo: 提交或打包时删去src
    public static Boolean isLexerOutput = false;
    public static Boolean isParserOutput = false;
    public static Boolean isErrorOutput = true;
}
