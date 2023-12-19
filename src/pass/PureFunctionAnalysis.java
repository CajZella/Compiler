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
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                for (Instr instr : block.getInstrs()) {
                    if (instr instanceof GetElementPtr) {
                        Value pointer = instr.getOperand(0);
                        if (pointer instanceof Argument)
                            function.isPure = false;
                    }
                }
            }
        }
    }
    private static void dfsFunction(Function function, HashSet<Function> visited) {
        visited.add(function);
        for (BasicBlock basicBlock : function.getBlocks()) {
            for (Instr instr : basicBlock.getInstrs()) {
                if (instr instanceof Call callInstr) { // 函数调用
                    Function callee = callInstr.getCallee();
                    if (callee.isBuiltin()) {// todo：可以优化
                        if (callee.getName().equals("@getint"))
                            function.isPure = false;
                    } else {
                        if (!visited.contains(callee))
                            dfsFunction(callee, visited);
                        function.isPure &= callee.isPure;
                    }
                } else if (instr instanceof Store || instr instanceof Load) {
                    Value pointer;
                    if (instr instanceof Load)
                        pointer = ((Load) instr).getPointer();
                    else
                        pointer = ((Store) instr).getPointer();
                    if (pointer instanceof GlobalVariable)
                        function.isPure = false;
                } else if (instr instanceof GetElementPtr) {
                    Value pointer = instr.getOperand(0);
                    if (pointer instanceof GlobalVariable && !((GlobalVariable) pointer).isString())
                        function.isPure = false;
                }
            }
        }
    }
}
