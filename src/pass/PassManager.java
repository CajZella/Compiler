package pass;

import frontend.ManageFrontend;
import ir.BasicBlock;
import ir.Module;
import ir.Function;
import ir.Value;
import ir.constants.Constant;
import ir.constants.ConstantInt;
import ir.instrs.Call;
import ir.instrs.Instr;
import ir.types.IntegerType;
import settings.Config;
import util.MyIO;

import java.util.ArrayList;

public class PassManager {
    public static void run(Module module) {
        MakeCFG.run(module);
        DeadCodeElimination deadCodeElimination = new DeadCodeElimination(module);
        Mem2reg mem2reg = new Mem2reg(module);
        new DeadControlFlowElimination(module).run(); // todo:可能没啥效果
        if (Config.isLLVMopt) {
            //new GlobalSymplify(module).run(); //todo: 有bug，如果使用全局变量的函数反复被调用
            MakeDom.run(module);
            mem2reg.run();
            new LVN(module).run();
            MyIO.writeFile(Config.LLVMFile, ManageFrontend.getModule().toString());
            deadCodeElimination.run();

            // 函数解释器 todo: 递归调用太多次会爆栈
            boolean finished = false;
            while (!finished) {
                finished = InterpreterPack.run(module);
                new LVN(module).run();
                deadCodeElimination.run();
            }

            new FunctionInline(module).run();

            new LVN(module).run();
            deadCodeElimination.run();
            MyIO.writeFile(Config.LLVMOptFile, ManageFrontend.getModule().toString());
            new RemovePhi(module).run();
        }

    }
}
