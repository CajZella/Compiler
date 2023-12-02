package pass;

import ir.BasicBlock;
import ir.Function;
import ir.GlobalVariable;
import ir.Module;
import ir.Use;
import ir.Value;
import ir.constants.ConstantInt;
import ir.instrs.Alloca;
import ir.instrs.Instr;
import ir.instrs.Load;
import ir.instrs.Phi;
import ir.instrs.Store;
import ir.types.IntegerType;
import ir.types.PointerType;
import util.MyLinkedList;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class Mem2reg {
    private final LinkedList<Function> functions;
    private HashSet<Instr> defInstrs = new HashSet<>();
    private HashSet<Instr> useInstrs = new HashSet<>();
    private HashSet<BasicBlock> useBBs = new HashSet<>();
    private HashSet<BasicBlock> defBBs = new HashSet<>();
    private Function curFunc;
    private MyLinkedList<BasicBlock> curBBs;
    private Alloca curVal;
    public Mem2reg(Module module) {
        this.functions = module.getFunctions();
    }
    public void run() {
//        for (Function function : functions)
//            if (!function.isBuiltin()) {
//                for (BasicBlock bb : function.getBlocks()) {
//                    System.out.print(String.format("%s's doms are", bb.getName()));
//                    for (BasicBlock dom : bb.getDoms())
//                        System.out.print(dom.getName() + " ");
//                    System.out.println();
//                }
//            }
//        for (Function function : functions)
//            if (!function.isBuiltin()) {
//                for (BasicBlock bb : function.getBlocks()) {
//                    System.out.println(String.format("%s's idom is %s", bb.getName(), bb.getIdom() == null ? "null" : bb.getIdom().getName()));
//                }
//            }
//        for (Function function : functions)
//            if (!function.isBuiltin()) {
//                for (BasicBlock bb : function.getBlocks()) {
//                    System.out.print(String.format("%s's DF are", bb.getName()));
//                    for (BasicBlock df : bb.getDFs())
//                        System.out.print(df.getName() + " ");
//                    System.out.println();
//                }
//            }
        insertPhi();
    }
    private void insertPhi() {
        for (Function function : functions)
            if (!function.isBuiltin()) {
                curFunc = function;
                curBBs = curFunc.getBlocks();
                for (BasicBlock bb : function.getBlocks())
                    for (Instr instr : bb.getInstrs())
                        if (instr instanceof Alloca && !((Alloca) instr).isArrayAlloc())
                            insertVarPhi((Alloca) instr);
            }
    }
    private void insertVarPhi(Alloca alloca) {
        curVal = alloca;
        defInstrs = new HashSet<>();
        useInstrs = new HashSet<>();
        useBBs = new HashSet<>();
        defBBs = new HashSet<>();
        for (Use use : alloca.getUseList()) { // 遍历所有使用该alloca的指令
            Instr instr = (Instr) use.getUser();
            if (instr instanceof Store) {
                defInstrs.add(instr);
                defBBs.add(instr.getParent());
            } else if (instr instanceof Load) {
                useInstrs.add(instr);
                useBBs.add(instr.getParent());
            }
        }
        if (useInstrs.isEmpty()) { // no use => delete define
            for (Instr defInstr : defInstrs) {
                defInstr.dropAllReferences();
                defInstr.remove();
            }
            alloca.dropAllReferences();
            alloca.remove();
        } else {
            /* step1. 找到需要加phi的基本块 */
            HashSet<BasicBlock> F = new HashSet<>(); // 需要加phi的基本块
            LinkedList<BasicBlock> W = new LinkedList<>();
            for (BasicBlock bb : defBBs) {
                W.add(bb);
            }
            while (!W.isEmpty()) {
                BasicBlock X = W.poll();
                for (BasicBlock Y : X.getDFs())
                    if (!F.contains(Y)) {
                        F.add(Y);
                        if (!defBBs.contains(Y))
                            W.add(Y);
                    }
            }
            /* step2. 在需要加phi的基本块中加入phi */
            for (BasicBlock bb : F) {
                Phi phi = new Phi(((PointerType) alloca.getType()).getReferencedType(), bb);
                bb.getEntryInstr().insertBefore(phi);
                useInstrs.add(phi);
                defInstrs.add(phi);
            }
            /* step3. 变量重命名 */
            reachingDefStack = new Stack<>();
            renameVarDFS(alloca.getParent());
            /* step4. 删除alloca, load, store指令 */
            for (Instr defInstr : defInstrs) {
                if (!(defInstr instanceof Phi)) {
                    defInstr.dropAllReferences();
                    defInstr.remove();
                } else {
                    Phi phi = (Phi) defInstr;
                    for (BasicBlock bb : defInstr.getParent().getPrecBBs()) {
                        if (!phi.hasIncomingFrom(bb))
                            phi.addIncoming(new ConstantInt(new IntegerType(32),0), bb);
                    }
                }
            }
            for (Instr useInstr : useInstrs) {
                if (!(useInstr instanceof Phi)) {
                    useInstr.dropAllReferences();
                    useInstr.remove();
                }
            }
            alloca.remove();
        }
    }
    private Stack<Value> reachingDefStack;
    private void renameVarDFS(BasicBlock entry) {
        Stack<Value> copy = (Stack<Value>) reachingDefStack.clone();
        for (Instr instr : entry.getInstrs()) {
            if (!(instr instanceof Phi) && useInstrs.contains(instr)) { // load val, ptr
                Value reachingDef = getReachingDef();
                instr.replaceAllUsesWith(reachingDef);
            }
            if (defInstrs.contains(instr)) {
                if (instr instanceof Store) // store val, ptr
                    reachingDefStack.push(((Store) instr).getValue());
                else if (instr instanceof Phi) // val = phi
                    reachingDefStack.push(instr);
            }
        }
        for (BasicBlock bb : entry.getSuccBBs()) {
            Instr instr = bb.getEntryInstr();
            if (instr instanceof Phi && useInstrs.contains(instr)) {
                Phi phi = (Phi) instr;
                phi.addIncoming(getReachingDef(), entry);
            }
        }
        // 前序遍历
        for (BasicBlock bb : entry.getIdoms())
            renameVarDFS(bb);
        reachingDefStack = copy;
    }
    /*
     * 根据v的定义链，找到支配i的最近定义点
     */
    private Value getReachingDef() {
        if (reachingDefStack.isEmpty())
            return new ConstantInt(new IntegerType(32), 0);
        else
            return reachingDefStack.peek();
    }
}
