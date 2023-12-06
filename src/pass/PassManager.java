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
        Mem2reg mem2reg = new Mem2reg(module);
        deadControlFlowElimination.run();
        MakeDom.run(module);
        if (Config.isLLVMopt) {
            mem2reg.run();
            deadCodeElimination.run();
            MyIO.writeFile(Config.LLVMFile, ManageFrontend.getModule().toString());
            new FunctionInline(module).run();
            deadCodeElimination.run();
            new GlobalSymplify(module).run();
            MakeDom.run(module);
            mem2reg.run();
            new LVN(module).run();
            deadCodeElimination.run();
            MyIO.writeFile(Config.LLVMOptFile, ManageFrontend.getModule().toString());
            new RemovePhi(module).run();
        }

    }
}
