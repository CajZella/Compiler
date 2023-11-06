package backend;

import backend.Optimize.RegAllocaTemp;
import backend.lir.MpModule;
import ir.Module;
import settings.Config;

public class BackEnd {
    private static Module module;
    private static MpModule mipsModule;
    public static void setModule(Module module) { BackEnd.module = module; }
    public static void run() {
        CodeGen codeGen = new CodeGen(module);
        codeGen.genModule();
        mipsModule = codeGen.getMipsModule();
        if (Config.isMIPSVROutput)
            System.out.print(mipsModule.toString());
        RegAllocaTemp regAllocaTemp = new RegAllocaTemp();
        regAllocaTemp.run(mipsModule);
    }
    public static MpModule getMipsModule() { return mipsModule; }
}
