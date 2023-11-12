package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import ir.instrs.Br;
import ir.instrs.Instr;
import ir.instrs.Ret;
import util.MyLinkedList;

import java.util.HashSet;
import java.util.LinkedList;

public class MakeCFG {
    private static LinkedList<Function> functions;
    public static void run(Module module) {
        MakeCFG.functions = module.getFunctions();
        init();
        /* step1. remove useless jump instruction */
        removeUselessJump();
        /* step2. remove dead basic block by dfs */
        removeDeadBB();
        /* step3. calculate CFG */
        makeCFG();
    }
    private static void init() {
        for (Function function : functions) {
            if (!function.isBuiltin())
                for (BasicBlock bb : function.getBlocks()) {
                    bb.getPrecBBs().clear();
                    bb.getSuccBBs().clear();
                }
        }
    }
    private static void removeUselessJump() {
        for (Function function : functions)
            if (!function.isBuiltin())
                removeFuncUselessJump(function);
    }
    private static void removeDeadBB() {
        for (Function function : functions)
            if (!function.isBuiltin())
                removeFuncDeadBB(function);
    }
    private static void makeCFG() {
        for (Function function : functions)
            if (!function.isBuiltin())
                makeFuncCFG(function);
    }
    private static void removeFuncUselessJump(Function function) {
        MyLinkedList<BasicBlock> bbs = function.getBlocks();
        for (BasicBlock bb : bbs) {
            boolean flag = false;
            for (Instr instr : bb.getInstrs()) {
                if (flag) {
                    instr.dropAllReferences(); // todo:维护use-def链
                    instr.remove();
                    continue;
                }
                if (instr.isTerminator())
                    flag = true;
            }
        }
    }
    private static void removeFuncDeadBB(Function function) {
        BasicBlock entry = function.getEntryBlock();
        HashSet<BasicBlock> visited = new HashSet<>();
        dfs(entry, visited);
        MyLinkedList<BasicBlock> bbs = function.getBlocks();
        for (BasicBlock bb : bbs) {
            if (!visited.contains(bb)) {
                bb.dropAllReferences(); // todo:维护use-def链
                bb.remove();
            }
        }
    }
    private static void dfs(BasicBlock bb, HashSet<BasicBlock> visited) {
        visited.add(bb);
        Instr terminator = bb.getTerminator();
        if (terminator instanceof Ret) return;
        Br br = (Br) terminator;
        if (br.isCondBr()) {
            BasicBlock trueBB = br.getTrueBB();
            BasicBlock falseBB = br.getFalseBB();
            if (!visited.contains(trueBB))
                dfs(trueBB, visited);
            if (!visited.contains(falseBB))
                dfs(falseBB, visited);
        } else {
            BasicBlock destBB = br.getDestBB();
            if (!visited.contains(destBB))
                dfs(destBB, visited);
        }
    }
    private static void makeFuncCFG(Function function) {
        MyLinkedList<BasicBlock> bbs = function.getBlocks();
        for (BasicBlock bb : bbs) {
            Instr terminator = bb.getTerminator();
            if (terminator instanceof Ret) continue;
            Br br = (Br) terminator;
            if (br.isCondBr()) {
                BasicBlock trueBB = br.getTrueBB();
                BasicBlock falseBB = br.getFalseBB();
                trueBB.addPrecBBs(bb);
                falseBB.addPrecBBs(bb);
                bb.addSuccBBs(trueBB);
                bb.addSuccBBs(falseBB);
            } else {
                BasicBlock destBB = br.getDestBB();
                destBB.addPrecBBs(bb);
                bb.addSuccBBs(destBB);
            }
        }
    }
}
