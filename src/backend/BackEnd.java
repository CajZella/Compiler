package backend;

import backend.lir.MpModule;
import ir.Module;

public class BackEnd {
    private static Module module;
    private static MpModule mipsModule;
    public static void setModule(Module module) { BackEnd.module = module; }
    public static void run() {
        CodeGen codeGen = new CodeGen(module);
        codeGen.genModule();
        mipsModule = codeGen.getMipsModule();
    }
    public static MpModule getMipsModule() { return mipsModule; }
}
