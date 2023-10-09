package settings;

import frontend.ErrorHandle.ErrorLog;
import frontend.LexParseLog;
import frontend.parser.Parser;
import frontend.parser.astNode.CompUnit;
import frontend.symbolTable.Sema;
import util.MyIO;

public class Configure {
    public static void run() {
        MyIO.readSourceFile(Config.sourceFile);
        Parser parser = new Parser();
        CompUnit compUnit;
        try {
            compUnit = parser.parseCompUnit();
            Sema sema = new Sema(compUnit);
            sema.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ErrorLog.hasError()) {
            ErrorLog.sort();
            if (Config.isErrorOutput)
                MyIO.writeFile(Config.errorFile, ErrorLog.print());
        } else {
            if (Config.isParserOutput)
                MyIO.writeFile(Config.targetFile, LexParseLog.print());
        }
        MyIO.closeReadFiles();
    }
}
