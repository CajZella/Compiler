package pass;
import ir.Module;
import settings.Config;

public class PassManager {
    public static void run(Module module) {
        MakeCFG.run(module);
        MakeDom.run(module);
        if (Config.isMem2reg)
            new Mem2reg(module).run();
    }
}
