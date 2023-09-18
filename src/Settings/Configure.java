package Settings;

import Lexer.Lexer;
import util.MyIO;

public class Configure {
    private static Lexer lexer;
    public static void run() {
        MyIO.readSourceFile(Config.sourceFile);
        MyIO.openTargetFile(Config.targetFile);
        lexer = new Lexer();
        lexer.run();
        lexDisplay();
        MyIO.closeFiles();
    }
    private static void lexDisplay() {
        if (Config.isLexerOutput) {
            MyIO.writeTargetFile(lexer.toString());
        }
    }
}
