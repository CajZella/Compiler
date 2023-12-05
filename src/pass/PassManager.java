package pass;
import frontend.ManageFrontend;
import ir.Module;
import settings.Config;
import util.MyIO;

public class PassManager {
    public static void run(Module module) {
        MakeCFG.run(module);
        DeadControlFlowElimination deadControlFlowElimination = new DeadControlFlowElimination(module);
        DeadCodeElimination deadCodeElimination = new DeadCodeElimination(module);
        deadControlFlowElimination.run();
        MakeDom.run(module);
        if (Config.isLLVMopt) {
            new Mem2reg(module).run();
            deadCodeElimination.run();
            new FunctionInline(module).run();
            deadCodeElimination.run();
            MyIO.writeFile(Config.LLVMOptFile, ManageFrontend.getModule().toString());
            new RemovePhi(module).run();
        }

    }
}
