package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import ir.Value;
import ir.constants.Constant;
import ir.constants.ConstantInt;
import ir.instrs.Call;
import ir.instrs.Instr;
import ir.types.IntegerType;

import java.util.ArrayList;

public class InterpreterPack {
    public static boolean run(Module module) {
        Boolean finished = true;
        PureFunctionAnalysis.run(module);
        for (Function function : module.getFunctions())
            for (BasicBlock block : function.getBlocks())
                for (Instr instr : block.getInstrs()) {
                    if (!(instr instanceof Call)) continue;
                    Call call = (Call) instr;
                    Function callee = call.getCallee();
                    boolean isConstant = true;
                    for (int i = 1; i < instr.operandsSize(); i++) {
                        Value operand = instr.getOperand(i);
                        if (!(operand instanceof ConstantInt)) isConstant = false;
                    }
                    if (callee.isPure && isConstant) {
                        finished = false;
                        ArrayList<Constant> args = new ArrayList<>();
                        for (int i = 1; i < instr.operandsSize(); i++)
                            args.add((Constant)instr.getOperand(i));
                        Interpreter interpreter = new Interpreter(args, callee, call);
                        int result = interpreter.interpretFunc();
                        call.replaceAllUsesWith(new ConstantInt(new IntegerType(32), result));
                        call.dropAllReferences();
                        call.remove();
                    }
                }
        return finished;
    }
}
