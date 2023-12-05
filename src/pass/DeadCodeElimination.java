package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import ir.Value;
import ir.instrs.Br;
import ir.instrs.Call;
import ir.instrs.Instr;
import ir.instrs.Ret;
import ir.instrs.Store;
import ir.types.FunctionType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class DeadCodeElimination {
    private Module module;
    public DeadCodeElimination(Module module) {
        this.module = module;
    }
    public boolean run() {
        boolean all = true;
        boolean finished = false;
        while (!finished) {
            finished = true;
            finished &= functionElimination();
            //finished &= codeElimination();
            all &= finished;
        }
        return all;
    }

    /*
     * 删除无用函数
     */
    private HashMap<Function, HashSet<Function>> calleeMap = new HashMap<>();
    private HashMap<Function, HashSet<Function>> callerMap = new HashMap<>();
    private HashSet<Function> sideEffectFunctions = new HashSet<>();
    private boolean functionElimination() {
        boolean finished = true;
        sideEffectFunctions.clear();
        Function mainFunction = null;
        HashSet<Function> visited = new HashSet<>();
        for (Function function : module.getFunctions()) {
            calleeMap.put(function, new HashSet<>());
            callerMap.put(function, new HashSet<>());
            if (function.isBuiltin())
                sideEffectFunctions.add(function);
            if (function.isMain())
                mainFunction = function;
        }
        dfsFunction(mainFunction, visited);
        for (Map.Entry<Function, HashSet<Function>> entry : calleeMap.entrySet())
            entry.getKey().setCallees(entry.getValue());
        for (Map.Entry<Function, HashSet<Function>> entry : callerMap.entrySet())
            entry.getKey().setCallers(entry.getValue());
        Iterator<Function> iterator = module.getFunctions().iterator();
        while(iterator.hasNext()) {
            Function function = iterator.next();
            if (function.isBuiltin() || function.isMain()) continue;
            if (callerMap.get(function).isEmpty()) {
                iterator.remove();
                finished = false;
            }
        }
        return finished;
    }
    // dfs遍历函数调用图
    private void dfsFunction(Function function, HashSet<Function> visited) {
        visited.add(function);
        for (BasicBlock basicBlock : function.getBlocks()) {
            for (Instr instr : basicBlock.getInstrs()) {
                if (instr instanceof Call) { // 函数调用
                    Call callInstr = (Call) instr;
                    Function callee = callInstr.getFunction();
                    if (callee.isBuiltin())
                        sideEffectFunctions.add(function);
                    else {
                        calleeMap.get(function).add(callee);
                        callerMap.get(callee).add(function);
                        if (!visited.contains(callee))
                            dfsFunction(callee, visited);
                        if (sideEffectFunctions.contains(callee))
                            sideEffectFunctions.add(function);
                    }
                } else if (instr instanceof Store)
                    sideEffectFunctions.add(function);
            }
        }
    }
    /*
     * 删除无用代码
     */
    private boolean codeElimination() {
        boolean finished = true;
        LinkedList<Instr> worklist = new LinkedList<>();
        HashSet<Instr> live = new HashSet<>();
        for (Function function : module.getFunctions()) {
            if (function.isBuiltin()) continue;
            for (BasicBlock block : function.getBlocks()) {
                for (Instr instr : block.getInstrs()) {
                    if (instr instanceof Call && sideEffectFunctions.contains(instr.getOperand(0))
                            || instr instanceof Ret
                            || instr instanceof Br
                            || instr instanceof Store)
                        worklist.add(instr);
                }
            }
        }
        while (!worklist.isEmpty()) {
            Instr instr = worklist.poll();
            live.add(instr);
            for (Value operand : instr.getOperands()) {
                if (operand instanceof Instr && !live.contains(operand))
                    worklist.add((Instr) operand);
            }
        }
        for (Function function : module.getFunctions()) {
            if (function.isBuiltin()) continue;
            for (BasicBlock block : function.getBlocks()) {
                Iterator<Instr> iterator = block.getInstrs().iterator();
                while (iterator.hasNext()) {
                    Instr instr = iterator.next();
                    if (!live.contains(instr)) {
                        instr.dropAllReferences();
                        instr.remove();
                        finished = false;
                    }
                }
            }
        }
        return finished;
    }
}