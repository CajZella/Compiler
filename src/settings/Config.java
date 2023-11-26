package settings;

public class Config {
    public static String sourceFile = "testfile.txt";
    public static String targetFile = "output.txt";
    public static String errorFile = "error.txt";
    public static String LLVMFile = "llvm_ir.txt";
    public static String LLVMOptFile = "llvm_opt.txt";
    public static String MIPSFile = "mips.txt";
    public static String MIPSVRFile = "mips_vr.txt";
    //public static Boolean isLexerOutput = false;
    public static Boolean isParserOutput = false;
    public static Boolean isErrorOutput = true;
    public static Boolean isLLVMIROutput = true;
    public static Boolean isMIPSOutput = true;
    public static Boolean isMIPSVROutput = true;
    public static Boolean isLLVMopt = true;
}
