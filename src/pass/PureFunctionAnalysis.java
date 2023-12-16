package pass;

import ir.Argument;
import ir.BasicBlock;
import ir.GlobalVariable;
import ir.Module;
import ir.Function;
import ir.Value;
import ir.instrs.Call;
import ir.instrs.GetElementPtr;
import ir.instrs.Instr;
import ir.instrs.Load;
import ir.instrs.Store;

import java.util.HashSet;


public class PureFunctionAnalysis {
    public static void run(Module module) {
        HashSet<Function> visited = new HashSet<>();
        Function mainFunction = null;
        for (Function function : module.getFunctions()) {
            if (function.isBuiltin())
                function.isPure = false;
            if (function.isMain())
                mainFunction = function;
        }
        dfsFunction(mainFunction, visited);
    }
    private static void dfsFunction(Function function, HashSet<Function> visited) {
        visited.add(function);
        for (BasicBlock basicBlock : function.getBlocks()) {
            for (Instr instr : basicBlock.getInstrs()) {
                if (instr instanceof Call callInstr) { // 函数调用
                    Function callee = callInstr.getCallee();
                    if (callee.isBuiltin()) // todo：可以优化
                        function.isPure = false;
                    else {
                        if (!visited.contains(callee))
                            dfsFunction(callee, visited);
                        if (!callee.isPure)
                            function.isPure = false;
                    }
                } else if (instr instanceof Store || instr instanceof Load) {
                    Value pointer;
                    if (instr instanceof Load)
                        pointer = ((Load) instr).getPointer();
                    else
                        pointer = ((Store) instr).getPointer();
                    if (pointer instanceof GlobalVariable)
                        function.isPure = false;
                    else if (pointer instanceof GetElementPtr
                            && (((GetElementPtr) pointer).getOperand(0) instanceof GlobalVariable)
                            || ((GetElementPtr) pointer).getOperand(0) instanceof Argument)
                        function.isPure = false;
                }
            }
        }
    }
}
