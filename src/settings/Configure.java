package settings;

import frontend.parser.Parser;
import util.MyIO;

public class Configure {
    public static void run() {
        MyIO.readSourceFile(Config.sourceFile);
        MyIO.openTargetFile(Config.targetFile);
        Parser parser = new Parser();
        try {
            parser.parseCompUnit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyIO.closeFiles();
    }
    public static void lexDisplay(String msg) {
        if (Config.isLexerOutput) {
            MyIO.writeTargetFile(msg + "\n");
        }
    }

    public static void parseDisplay(String msg) {
        if(Config.isParserOutput) {
            MyIO.writeTargetFile(msg + "\n");
        }
    }
}
