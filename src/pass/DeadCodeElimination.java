package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import ir.Value;
import ir.constants.ConstantInt;
import ir.instrs.Br;
import ir.instrs.Call;
import ir.instrs.Instr;
import ir.instrs.Phi;
import ir.instrs.Store;
import util.MyLinkedList;

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
    public void run() {
        boolean finished = false;
        while (!finished) {
            finished = true;
            finished &= functionElimination();
            finished &= codeElimination();
            finished &= deleteRedundantPhi();
            finished &= mergeRedundantBranch();
            finished &= mergeBlock();
        }
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
                    Function callee = callInstr.getCallee();
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
            Iterator<BasicBlock> iterator = function.getBlocks().iterator();
            while (iterator.hasNext()) {
                BasicBlock block = iterator.next();
                if (block.getPrecBBs().isEmpty() && function.getEntryBlock() != block) {
                    for (BasicBlock succBB : block.getSuccBBs()) {
                        succBB.getPrecBBs().remove(block);
                    }
                    for (Instr instr : block.getInstrs()) {
                        instr.dropAllReferences();
                        instr.remove();
                    }
                    for (BasicBlock block1 : block.getSuccBBs()) {
                        for (Instr instr : block1.getInstrs()) {
                            if (instr instanceof Phi)
                                ((Phi)instr).remove(block);
                        }
                    }
                    iterator.remove();
                    continue;
                }
                for (Instr instr : block.getInstrs()) {
                    if (instr.getValueTy() == Value.ValueType.call && sideEffectFunctions.contains(instr.getOperand(0))
                            || instr.getValueTy() == Value.ValueType.ret
                            || instr.getValueTy() == Value.ValueType.br
                            || instr.getValueTy() == Value.ValueType.store) {
                        worklist.add(instr);
                    }
                }
            }
        }
        while (!worklist.isEmpty()) {
            Instr instr = worklist.poll();
            live.add(instr);
            for (Value operand : instr.getOperands()) {
                if (operand instanceof Instr && !live.contains(operand)) {
                    worklist.add((Instr) operand);
                }
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
    private boolean deleteRedundantPhi() {
        boolean finished = true;
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                Iterator<Instr> iterator = block.getInstrs().iterator();
                while(iterator.hasNext()) {
                    Instr instr = iterator.next();
                    if (!(instr instanceof Phi))
                        continue;
                    Phi phi = (Phi) instr;
                    if (phi.operandsSize() == 1) {
                        finished = false;
                        phi.replaceAllUsesWith(phi.getOperand(0));
                        iterator.remove();
                    }
                }
            }
        }
        return finished;
    }
    private boolean mergeRedundantBranch() {
        boolean finished = true;
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                Instr terminator = block.getTerminator();
                if (terminator instanceof Br) {
                    Br br = (Br) terminator;
                    if (br.isCondBr() && br.getTrueBB().equals(br.getFalseBB())) {
                        finished = false;
                        br.replaceAllUses(br.getTrueBB());
                    }
                    else if (br.isCondBr() && br.getOperand(0) instanceof ConstantInt) {
                        finished = false;
                        ConstantInt cond = (ConstantInt) br.getOperand(0);
                        BasicBlock toBlock;
                        if (cond.getVal() == 0) {
                            toBlock = br.getTrueBB();
                            br.replaceAllUses(br.getFalseBB());
                        } else {
                            toBlock = br.getFalseBB();
                            br.replaceAllUses(br.getTrueBB());
                        }
                        block.getSuccBBs().remove(toBlock);
                        toBlock.getPrecBBs().remove(block);
                        for (Instr instr : toBlock.getInstrs()) {
                            if (instr instanceof Phi)
                                ((Phi) instr).remove(block);
                        }
                    }
                }
            }
        }
        return finished;
    }
    private boolean mergeBlock() {
        boolean finished = true;
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                if (block.getSuccBBs().size() == 1
                        && block.getSuccBBs().iterator().next().getPrecBBs().size() == 1) {
                    finished = false;
                    BasicBlock succBlock = block.getSuccBBs().iterator().next();
                    MyLinkedList<Instr> instrs = block.getInstrs();
                    instrs.remove(instrs.getTail());
                    for (Instr instr : succBlock.getInstrs())
                        instr.setParent(block);
                    instrs.addAll(succBlock.getInstrs());

                    for (BasicBlock succ : succBlock.getSuccBBs())
                        for (Instr instr : succ.getInstrs())
                            if (instr instanceof Phi)
                                ((Phi) instr).replacePhiBB(succBlock, block);

                    block.getSuccBBs().remove(succBlock);
                    block.getSuccBBs().addAll(succBlock.getSuccBBs());
                    for (BasicBlock succ : succBlock.getSuccBBs()) {
                        succ.getPrecBBs().remove(succBlock);
                        succ.getPrecBBs().add(block);
                    }
                    succBlock.remove();
                }
            }
        }
        return finished;
    }
    private boolean deleteEmptyBlock() {
        boolean finished = true;
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                Instr entry = block.getEntryInstr();
                if (entry.getValueTy() == Value.ValueType.br && !((Br) entry).isCondBr() && !block.getPrecBBs().isEmpty()) {
                    finished = false;
                    BasicBlock toBlock = (BasicBlock) entry.getOperand(0);
                    for (Instr instr : toBlock.getInstrs())
                        if (instr instanceof Phi)
                            ((Phi) instr).replacePhiBB(block, block.getPrecBBs());
                    for (BasicBlock precBlock : block.getPrecBBs()) {
                        Br terminator = (Br) precBlock.getTerminator();
                        terminator.replaceUsesOfWith(block, toBlock);
                        precBlock.getSuccBBs().remove(block);
                        precBlock.getSuccBBs().add(toBlock);
                        toBlock.getPrecBBs().add(precBlock);
                    }
                    toBlock.getPrecBBs().remove(block);
                    block.remove();
                }
            }
        }
        return finished;
    }
}