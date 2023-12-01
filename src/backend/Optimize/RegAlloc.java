package backend.Optimize;

import backend.lir.MpBlock;
import backend.lir.MpFunction;
import backend.lir.MpModule;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpLoad;
import backend.lir.mipsInstr.MpMove;
import backend.lir.mipsInstr.MpStore;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpReg;
import util.MyPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/*
 * step1. 全局活跃变量分析（pre:控制流）
 */
public class RegAlloc {
    private MpModule mipsModule;
    private MpFunction curMF;
    private int stackSize;
    private ArrayList<MpReg> precolored;
    private HashSet<MpReg> initial = new HashSet<>(); // 所有的寄存器
    private HashSet<MpReg> simplifyWorklist = new HashSet<>(); // 低度数的传送无关的节点表
    private HashSet<MpReg> freezeWorklist = new HashSet<>(); // 低度数的传送相关的节点表
    private HashSet<MpReg> spillWorklist = new HashSet<>(); // 高度数的节点表
    private HashSet<MpReg> spilledNodes = new HashSet<>(); // 需要溢出的节点表
    private HashSet<MpReg> coalescedNodes = new HashSet<>(); // 合并的节点表
    private HashSet<MpReg> coloredNodes = new HashSet<>(); // 已着色的节点表
    private Stack<MpReg> selectStack = new Stack<>(); // 选择栈

    // 传送指令
    private HashSet<MpMove> coalescedMoves = new HashSet<>(); // 已经合并的传送指令集合
    private HashSet<MpMove> constrainedMoves = new HashSet<>(); // 源操作数和目标操作数冲突的传送指令集合
    private HashSet<MpMove> frozenMoves = new HashSet<>(); // 不再考虑合并的传送指令集合
    private HashSet<MpMove> worklistMoves = new HashSet<>(); // 有可能合并的传送指令集合
    private HashSet<MpMove> activeMoves = new HashSet<>(); // 还未做好合并准备的传送指令集合

    // 其他数据结构
    private HashSet<MyPair<MpReg, MpReg>> adjSet = new HashSet<>(); // 图中冲突边（u，v）的集合
    private HashMap<MpReg, HashSet<MpReg>> adjList = new HashMap<>(); // 图的邻接表示
    private HashMap<MpReg, Integer> degree = new HashMap<>(); // 包含每个节点当前度数的数组
    private HashMap<MpReg, HashSet<MpMove>> moveList = new HashMap<>(); // 从一个节点到与该结点相关的传送指令表的映射
    private HashMap<MpReg, MpReg> alias = new HashMap<>(); // 传送指令(u,v)合并后，alias[v] = u
    private HashMap<MpReg, MpReg> color = new HashMap<>(); // 算法为节点选择的颜色
    private int K;
    public RegAlloc(MpModule mipsModule, ArrayList<MpReg> precolored) {
        this.mipsModule = mipsModule;
        this.precolored = precolored;
    }
    public void run() {
        for (MpFunction mipsFunction : mipsModule.getMpFunctions()) {
            curMF = mipsFunction;
            init();
            regAlloc();
            curMF.setStackSize(stackSize);
            regReset();
        }
    }
    private void regReset() {
        for (MpBlock block : curMF.getMpBlocks())
            for (MpInstr instr : block.getMpInstrs()) {
                if (instr.hasDstReg() && !instr.getDstReg().isColored())
                    instr.replaceDst(color.get(instr.getDstReg()));
                if (instr.hasSrc1Reg() && !instr.getSrc1Reg().isColored())
                    instr.replaceSrc1(color.get(instr.getSrc1Reg()));
                if (instr.hasSrc2Reg() && !instr.getSrc2Reg().isColored())
                    instr.replaceSrc2(color.get(instr.getSrc2Reg()));
            }
    }
    private void init() {
        initial.clear();
        simplifyWorklist.clear();
        freezeWorklist.clear();
        spillWorklist.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        coloredNodes.clear();
        selectStack.clear();
        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        worklistMoves.clear();
        activeMoves.clear();
        adjSet.clear();
        adjList.clear();
        degree.clear();
        moveList.clear();
        alias.clear();
        color.clear();
        for (MpBlock block : curMF.getMpBlocks())
            for (MpInstr instr : block.getMpInstrs()) {
                for (MpReg reg : instr.getUseRegs()) {
                    if (!reg.isColored()) {
                        initial.add(reg);
                        adjList.put(reg, new HashSet<>());
                        degree.put(reg, 0);
                    }
                }
                for (MpReg reg : instr.getDefRegs()) {
                    if (!reg.isColored()) {
                        initial.add(reg);
                        adjList.put(reg, new HashSet<>());
                        degree.put(reg, 0);
                    }
                }
            }

        if (curMF.getLabel().getName().equals("main"))
            K = 23;
        else
            K = 13;
        stackSize = curMF.getStackSize();
    }
    private void regAlloc() {
        build();
        makeWorklist();
        // 直到不再产生溢出
        while (!simplifyWorklist.isEmpty() || !worklistMoves.isEmpty() || !freezeWorklist.isEmpty() || !spillWorklist.isEmpty()) {
            if (!simplifyWorklist.isEmpty())
                simplify();
            else if (!worklistMoves.isEmpty())
                coalesce();
            else if (!freezeWorklist.isEmpty())
                freeze();
            else
                selectSpill();
        }
        assignColors();
        // 当assign colors 生成了溢出时，rewrite program要为被溢出的临时变量分配存储单元，并插入访问这些单元的存/取指令，这些存取指令访问新创建的临时变量
        if (!spilledNodes.isEmpty()) {
            rewriteProgram();
            regAlloc();
        }
    }
    /*
     * 构造冲突图，以及moveList（记录所有move指令）
     */
    private void build() {
        LivenessAnalysis livenessAnalysis = new LivenessAnalysis(curMF);
        HashMap<MpBlock, HashSet<MpReg>> out = livenessAnalysis.getOut();
        for (MpBlock block : curMF.getMpBlocks()) {
            HashSet<MpReg> live = out.get(block);
            MpInstr instr = block.getLastMpInstr(); // 沿控制流反向遍历
            while (true) {
                if (instr instanceof MpMove) {
                    MpMove move = (MpMove) instr;
                    live.removeAll(instr.getUseRegs());
                    HashSet<MpReg> regs = new HashSet<>();
                    regs.addAll(instr.getDefRegs());
                    regs.addAll(instr.getUseRegs());
                    for (MpReg reg1 : regs) {
                        HashSet<MpMove> set = moveList.getOrDefault(reg1, new HashSet<>());
                        set.add(move);
                        moveList.put(reg1, set);
                    }
                    worklistMoves.add(move);
                }
                live.addAll(instr.getDefRegs());
                for (MpReg D : instr.getDefRegs())
                    for(MpReg L : live)
                        addEdge(L, D);
                live.removeAll(instr.getDefRegs());
                live.addAll(instr.getUseRegs());
                if (instr == block.getFirstMpInstr())
                    break;
                instr = (MpInstr) instr.getPrev();
            }
        }
    }
    private void addEdge(MpReg U, MpReg V) {
        MyPair<MpReg, MpReg> pair = new MyPair<>(U, V);
        if (U.equal(V) || adjSet.contains(pair)) return;
        adjSet.add(pair);
        adjSet.add(new MyPair<>(V, U));
        if (!precolored.contains(U)) {
            adjList.get(U).add(V);
            degree.put(U, degree.get(U) + 1);
        }
        if (!precolored.contains(V)) {
            adjList.get(V).add(U);
            degree.put(V, degree.get(V) + 1);
        }
    }
    private void makeWorklist() {
        for (MpReg reg : initial) {
            if (degree.get(reg) >= K)
                spillWorklist.add(reg);
            else if (moveRelated(reg))
                freezeWorklist.add(reg);
            else
                simplifyWorklist.add(reg);
        }
        initial.clear();
    }
    private HashSet<MpMove> nodeMoves(MpReg reg) {
        HashSet<MpMove> moves = new HashSet<>();
        moves.addAll(activeMoves);
        moves.addAll(worklistMoves);
        if (moveList.containsKey(reg))
            moves.retainAll(moveList.get(reg));
        return moves;
    }
    private boolean moveRelated(MpReg reg) {
        return !nodeMoves(reg).isEmpty();
    }
    private void simplify() {
        MpReg reg = simplifyWorklist.iterator().next();
        simplifyWorklist.remove(reg);
        selectStack.push(reg);
        for (MpReg reg1 : adjacent(reg))
            decrementDegree(reg1);
    }
    private HashSet<MpReg> adjacent(MpReg reg) {
        HashSet<MpReg> regs = new HashSet<>();
        regs.addAll(adjList.get(reg));
        regs.removeAll(selectStack);
        regs.removeAll(coalescedNodes);
        return regs;
    }
    private void decrementDegree(MpReg reg) {
        int d = degree.get(reg);
        degree.put(reg, d - 1);
        if (d == K) {
            HashSet<MpReg> regs = adjacent(reg);
            regs.add(reg);
            enableMoves(regs); // 处理邻节点相关的传送指令
            spillWorklist.remove(reg);
            if (moveRelated(reg))
                freezeWorklist.add(reg);
            else
                simplifyWorklist.add(reg);
        } // 否则，是传送相关的节点
    }
    private void enableMoves(HashSet<MpReg> regs) {
        for (MpReg reg : regs)
            for (MpMove move : nodeMoves(reg))
                if (activeMoves.contains(move)) {
                    activeMoves.remove(move);
                    worklistMoves.add(move);
                }
    }
    private void coalesce() { // 合并阶段只考虑work list moves 中的传送指令
        MpMove move = worklistMoves.iterator().next();
        MpReg x = getAlias(move.getSrc1Reg());
        MpReg y = getAlias(move.getDstReg()); // copy(x,y) 不知道顺序对不对
        MpReg u, v;
        if (precolored.contains(y)) {
            u = y;
            v = x;
        } else {
            u = x;
            v = y;
        }
        worklistMoves.remove(move);
        if (u.equal(v)) {
            coalescedMoves.add(move);
            addWorkList(u);
        } else if (precolored.contains(v) || adjSet.contains(new MyPair<>(u, v))) {
            constrainedMoves.add(move);
            addWorkList(u);
            addWorkList(v);
        } else {
            boolean flag = true;
            if (precolored.contains(u)) {
                boolean allOk = true;
                for (MpReg reg : adjacent(v))
                    if (!OK(reg, u)) {
                        allOk = false;
                        break;
                    }
                if (allOk) {
                    coalescedMoves.add(move);
                    combine(u, v);
                    addWorkList(u);
                    flag = false;
                }
            }
            else {
                HashSet regs = adjacent(u);
                regs.addAll(adjacent(v));
                if (conservative(regs)) {
                    coalescedMoves.add(move);
                    combine(u, v);
                    addWorkList(u);
                    flag = false;
                }
            }
            if (flag)
                activeMoves.add(move);
        }
    }
    private void addWorkList(MpReg reg) { // 合并传送指令，两个节点可能不再是传送有关，用addWorkList将它们加入简化工作表
        if (!precolored.contains(reg) && !moveRelated(reg) && degree.get(reg) < K) {
            freezeWorklist.remove(reg);
            simplifyWorklist.add(reg);
        }
    }
    private boolean OK(MpReg u, MpReg v) { // 合并一个预着色寄存器时所使用的启发式函数
        return degree.get(u) < K || precolored.contains(u) || adjSet.add(new MyPair<>(u, v));
    }
    private boolean conservative(HashSet<MpReg> regs) { // 保守合并启发式的函数
        int k = 0;
        for (MpReg reg : regs)
            if (degree.get(reg) >= K)
                k++;
        return k < K;
    }
    private MpReg getAlias(MpReg reg) {
        if (coalescedNodes.contains(reg))
            return getAlias(alias.get(reg));
        else
            return reg;
    }
    private void combine(MpReg u, MpReg v) {
        if (freezeWorklist.contains(v))
            freezeWorklist.remove(v);
        else
            spillWorklist.remove(v);
        coalescedNodes.add(v);
        alias.put(v, u);
        moveList.get(u).addAll(moveList.get(v));
        HashSet<MpReg> regs = new HashSet<>();
        regs.add(v);
        enableMoves(regs);
        for (MpReg reg : adjacent(v)) {
            addEdge(reg, u);
            decrementDegree(reg);
        }
        if (degree.containsKey(u) && degree.get(u) >= K && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }
    private void freeze() {
        MpReg reg = freezeWorklist.iterator().next();
        freezeWorklist.remove(reg);
        simplifyWorklist.add(reg);
        freezeMoves(reg);
    }
    private void freezeMoves(MpReg reg) {
        for (MpMove move : nodeMoves(reg)) {
            MpReg x = move.getSrc1Reg();
            MpReg y = move.getDstReg(); // todo: 不知道顺序对不对
            MpReg v;
            if (getAlias(y).equal(getAlias(reg)))
                v = getAlias(x);
            else
                v = getAlias(y);
            activeMoves.remove(move);
            frozenMoves.add(move);
            if (nodeMoves(v).isEmpty() && degree.get(v) < K) {
                freezeWorklist.remove(v);
                simplifyWorklist.add(v);
            }
        }
    }
    private void selectSpill() {
        //todo: 使用次数和是否在循环中结合考虑
        int maxCost = 0;
        MpReg reg = null;
        for (MpReg reg1 : spillWorklist)
            if (calcSpillCost(reg1) > maxCost) {
                maxCost = calcSpillCost(reg1);
                reg = reg1;
            }
        spillWorklist.remove(reg);
        simplifyWorklist.add(reg);
        freezeMoves(reg);
    }
    private int calcSpillCost(MpReg reg) {
        int cost = 0;
        if (reg.getLoopDepth() >= 1 && reg.getLoopDepth() <= 3)
            cost += Math.pow(100, reg.getLoopDepth());
        else if (reg.getLoopDepth() <= 8)
            cost += Math.pow(10, reg.getLoopDepth());
        else
            cost += reg.getLoopDepth() * 100;
        cost += reg.getUseTime();
        return cost;
    }
    private void assignColors() {
        while (!selectStack.isEmpty()) {
            MpReg reg = selectStack.pop();
            ArrayList<MpReg> okColors = new ArrayList<>();
            if (K == 24)
                for (int i = 25; i >= 3; i--)
                    okColors.add(precolored.get(i));
            else
                for (int i = 15; i >= 3; i--)
                    okColors.add(precolored.get(i));

            for (MpReg w : adjList.get(reg))
                if (coloredNodes.contains(getAlias(w)) || precolored.contains(getAlias(w)))
                    okColors.remove(color.get(getAlias(w)));
            if (okColors.isEmpty())
                spilledNodes.add(reg);
            else {
                coloredNodes.add(reg);
                color.put(reg, okColors.get(0));
            }
        }
        for (MpReg reg : coalescedNodes)
            color.put(reg, color.get(getAlias(reg)));
    }
    private void rewriteProgram() { // process spill nodes:
        HashSet<MpReg> newTemps = new HashSet<>();
        for (MpReg v : spilledNodes) {
            MpImm offset = new MpImm(stackSize);
            stackSize += 4;
            for (MpBlock block : curMF.getMpBlocks()) {
                for (MpInstr instr : block.getMpInstrs()) {
                    for (MpReg defReg : instr.getDefRegs()) {
                        if (defReg.equal(v)) {
                            MpReg newTemp = new MpReg();
                            newTemps.add(newTemp);
                            instr.replaceDst(newTemp);
                            instr.insertAfter(new MpStore(block, newTemp, precolored.get(29), offset));
                        }
                    }
                    for (MpReg useReg : instr.getUseRegs()) {
                        if (useReg.equal(v)) {
                            MpReg newTemp = new MpReg();
                            newTemps.add(newTemp);
                            instr.replaceSrc(newTemp);
                            instr.insertBefore(new MpLoad(block, newTemp, precolored.get(29), offset));
                        }
                    }
                }
            }
        }
        spilledNodes.clear();
        initial.addAll(coloredNodes);
        initial.addAll(coalescedNodes);
        initial.addAll(newTemps);
        coloredNodes.clear();
        coalescedNodes.clear();
    }
}
