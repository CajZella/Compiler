package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import util.MyLinkedList;

import java.util.HashSet;
import java.util.LinkedList;

public class MakeDom {
    private static LinkedList<Function> functions;
    public static void run(Module module) {
        MakeDom.functions = module.getFunctions();
        init();
        /* step1. make dominator relationship */
        makeDom();
        /* step2. make immediate dominator relationship */
        makeIdom();
        /* step3. make dominate frontier */
        makeDF();
    }
    private static void init() {
        for (Function function : functions) {
            for (BasicBlock bb : function.getBlocks()) {
                bb.getDoms().clear();
                bb.getIdoms().clear();
                bb.getDFs().clear();
                bb.setIdom(null);
            }
        }
    }
    private static void makeDom() {
        for (Function function : functions)
            if (!function.isBuiltin())
                makeFuncDom(function);
    }
    private static void makeIdom() {
        for (Function function : functions)
            if (!function.isBuiltin())
                makeFuncIdom(function);
    }
    private static void makeDF() {
        for (Function function : functions)
            if (!function.isBuiltin())
                makeFuncDF(function);
    }
    /*
     * 结点删除法：
     * 如果我们删去图中的某一个结点后，有一些结点变得不可到达，那么这个被删去的结点支配这些变得不可到达的结点
     */
    private static void makeFuncDom(Function function) {
        MyLinkedList<BasicBlock> bbs = function.getBlocks();
        HashSet<BasicBlock> visited = new HashSet<>();
        for (BasicBlock bb : bbs) {
            visited.clear();
            dfs(bbs.getHead(), bb, visited);
            for (BasicBlock bb1 : bbs)
                if (!visited.contains(bb1))
                    bb1.addDom(bb);
        }
    }
    private static void dfs(BasicBlock entry, BasicBlock del, HashSet<BasicBlock> visited) {
        if (entry.equals(del))
            return;
        visited.add(entry);
        for (BasicBlock bb : entry.getSuccBBs()) {
            if (!visited.contains(bb) && !bb.equals(del)) {
                dfs(bb, del, visited);
            }
        }
    }
    /*
    * 参考 https://oi-wiki.org/graph/dominator-tree/
    * */
    private static void makeFuncIdom(Function function) {
        MyLinkedList<BasicBlock> bbs = function.getBlocks();
        for (BasicBlock U : bbs) {
            for (BasicBlock V : U.getDoms()) {
                HashSet<BasicBlock> tmp1 = new HashSet<>(U.getDoms());
                tmp1.retainAll(V.getDoms());
                HashSet<BasicBlock> tmp = new HashSet<>(U.getDoms());
                tmp.removeAll(tmp1);
                if (tmp.size() == 1 && tmp.contains(U)) {
                    U.setIdom(V);
                    V.addIdom(U);
                    break;
                }

            }
        }
    }
    private static void makeFuncDF(Function function) {
        MyLinkedList<BasicBlock> bbs = function.getBlocks();
        for (BasicBlock A : bbs) {
            if (A.getPrecBBs().size() > 1) {
                for (BasicBlock B : A.getPrecBBs()) {
                    BasicBlock X = B;
                    while (!X.equals(A.getIdom())) {
                        X.addDF(A);
                        X = X.getIdom();
                    }
                }
            }
        }
//        for (BasicBlock A : bbs) {
//            for (BasicBlock B : A.getSuccBBs()) {
//                BasicBlock X = A;
//                while (!X.getDoms().contains(B) && !X.equals(B)) {
//                    X.addDF(B);
//                    X = X.getIdom();
//                    if (null == X) break;
//                }
//            }
//        }
    }
}
