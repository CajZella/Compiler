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
        if (Config.isLLVMopt) {
            new GlobalSymplify(module).run();
            MakeDom.run(module);
            mem2reg.run();
            new LVN(module).run();
            MyIO.writeFile(Config.LLVMFile, ManageFrontend.getModule().toString());
            deadCodeElimination.run();

            // 函数解释器
            PureFunctionAnalysis.run(module);
            for (Function function : module.getFunctions())
                for (BasicBlock block : function.getBlocks())
                    for (Instr instr : block.getInstrs()) {
                        if (!(instr instanceof Call && instr.getType().isIntegerTy())) continue;
                        Call call = (Call) instr;
                        Function callee = call.getCallee();
                        boolean isConstant = true;
                        for (int i = 1; i < instr.operandsSize(); i++) {
                            Value operand = instr.getOperand(i);
                            if (!(operand instanceof ConstantInt)) isConstant = false;
                        }
                        if (callee.isPure && isConstant) {
                            ArrayList<Constant> args = new ArrayList<>();
                            for (int i = 1; i < instr.operandsSize(); i++)
                                args.add((Constant)instr.getOperand(i));
                            Interpreter interpreter = new Interpreter(args, callee, call);
                            int result = interpreter.interpretFunc();
                            call.replaceAllUsesWith(new ConstantInt(new IntegerType(32), result));
                            call.remove();
                        }
                    }

            new LVN(module).run();
            deadCodeElimination.run();
            new FunctionInline(module).run();

            new LVN(module).run();
            deadCodeElimination.run();
            MyIO.writeFile(Config.LLVMOptFile, ManageFrontend.getModule().toString());
            new RemovePhi(module).run();
        }

    }
}
