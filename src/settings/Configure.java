package settings;

import backend.BackEnd;
import frontend.ErrorHandle.ErrorLog;
import frontend.LexParseLog;
import ir.Module;
import frontend.ManageFrontend;
import pass.PassManager;
import util.MyIO;

public class Configure {
    public static void run() {
        MyIO.readSourceFile(Config.sourceFile);
        ManageFrontend.run();
        Module module = ManageFrontend.getModule();
        if (ErrorLog.hasError()) {
            ErrorLog.sort();
            if (Config.isErrorOutput)
                MyIO.writeFile(Config.errorFile, ErrorLog.print());
        } else {
            if (Config.isParserOutput)
                MyIO.writeFile(Config.targetFile, LexParseLog.print());
            if (Config.isLLVMIROutput) {
                MyIO.writeFile(Config.LLVMFile, ManageFrontend.getModule().toString());
                PassManager.run(module);
            }
            if (Config.isMIPSOutput) {
                BackEnd.setModule(module);
                BackEnd.run();
                MyIO.writeFile(Config.MIPSFile, BackEnd.getMipsModule().toString());
            }
        }
        MyIO.closeReadFiles();
    }
}
