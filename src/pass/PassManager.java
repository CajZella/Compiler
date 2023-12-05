package pass;
import frontend.ManageFrontend;
import ir.Module;
import settings.Config;
import util.MyIO;

public class PassManager {
    public static void run(Module module) {
        MakeCFG.run(module);
        DeadControlFlowElimination deadControlFlowElimination = new DeadControlFlowElimination(module);
        deadControlFlowElimination.run();
        MakeDom.run(module);
        if (Config.isLLVMopt) {
            new Mem2reg(module).run();
            DeadCodeElimination deadCodeElimination = new DeadCodeElimination(module);
            deadCodeElimination.run();
            boolean finished = false;
//            while (!finished) {
//                finished = true;
//                finished &= deadCodeElimination.run();
//            }
            MyIO.writeFile(Config.LLVMOptFile, ManageFrontend.getModule().toString());
            new RemovePhi(module).run();
        }

    }
}
