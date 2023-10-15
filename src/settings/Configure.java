package settings;

import frontend.ErrorHandle.ErrorLog;
import frontend.LexParseLog;
import frontend.parser.Parser;
import frontend.parser.astNode.CompUnit;
import frontend.ManageFrontend;
import util.MyIO;

public class Configure {
    public static void run() {
        MyIO.readSourceFile(Config.sourceFile);
        ManageFrontend.run();
        if (ErrorLog.hasError()) {
            ErrorLog.sort();
            if (Config.isErrorOutput)
                MyIO.writeFile(Config.errorFile, ErrorLog.print());
        } else {
            if (Config.isParserOutput)
                MyIO.writeFile(Config.targetFile, LexParseLog.print());
            if (Config.isLLVMIROutput)
                MyIO.writeFile(Config.LLVMFile, ManageFrontend.getModule().toString());
        }
        MyIO.closeReadFiles();
    }
}
