package settings;

public class Config {
    public static String sourceFile = "testfile.txt"; // todo: 提交或打包时删去src
    public static String targetFile = "output.txt"; // todo: 提交或打包时删去src
    public static String errorFile = "error.txt"; // todo: 提交或打包时删去src
    public static String LLVMFile = "llvm.txt"; // todo: 提交或打包时删去src
    public static Boolean isLexerOutput = false;
    public static Boolean isParserOutput = false;
    public static Boolean isErrorOutput = true;
    public static Boolean isLLVMIROutput = true;
}
