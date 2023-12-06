package backend;

import backend.Optimize.DivByConst;
import backend.lir.MpBlock;
import backend.lir.mipsInstr.MpAlu;
import backend.lir.mipsInstr.MpBranch;
import backend.lir.mipsInstr.MpCmp;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpJump;
import backend.lir.mipsInstr.MpLoad;
import backend.lir.mipsInstr.MpLoadAddr;
import backend.lir.mipsInstr.MpLoadImm;
import backend.lir.mipsInstr.MpMfhi;
import backend.lir.mipsInstr.MpMove;
import backend.lir.mipsInstr.MpShift;
import backend.lir.mipsInstr.MpStore;
import backend.lir.mipsInstr.MpSyscall;
import backend.lir.mipsOperand.MpData;
import backend.lir.MpFunction;
import backend.lir.MpModule;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;
import backend.lir.mipsOperand.MpStackOffset;
import ir.BasicBlock;
import ir.Function;
import ir.GlobalVariable;
import ir.Module;
import ir.Value;
import ir.constants.Constant;
import ir.constants.ConstantArray;
import ir.constants.ConstantInt;
import ir.constants.ConstantStr;
import ir.instrs.Alloca;
import ir.instrs.Alu;
import ir.instrs.Br;
import ir.instrs.Call;
import ir.instrs.GetElementPtr;
import ir.instrs.Icmp;
import ir.instrs.Instr;
import ir.instrs.Load;
import ir.instrs.Phi;
import ir.instrs.Ret;
import ir.instrs.Store;
import ir.instrs.Trunc;
import ir.instrs.Zext;
import ir.types.ArrayType;
import ir.types.PointerType;
import ir.types.Type;
import pass.PCs;
import settings.Config;
import util.MyLinkedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

// 不涉及栈的管理，这需要在寄存器分配后，在这里还是用虚拟寄存器
public class CodeGen {
    private Module irModule;
    private MpModule mipsModule;
    private ArrayList<MpReg> mipsPhyRegs;
    private MpBlock curMB = null;
    private MpFunction curMF = null;
    private BasicBlock curIB = null;
    private Function curIF = null;
    private int curMFOffset;
    // 映射
    /*
     * irFunction到mipsFunction的映射
     * 作用:
     */
    private HashMap<Function, MpFunction> f2mf = new HashMap<>();
    /*
     * irBasicBlock到mipsBasicBloc的映射
     * 作用:
     */
    private HashMap<BasicBlock, MpBlock> bb2mb = new HashMap<>();
    /*
     * irValue到mipsOperand的映射
     * 作用：后续指令需要用到先前的寄存器值，方便查找
     * mipsOperand包括 reg 和 MpImm, MpStackOffset
     */
    private HashMap<Value, MpOpd> val2opd = new HashMap<>();
    /*
     * irGlobalVariable到mipsData的映射
     * 方便后续要用到globalVariable的指令
     */
    private HashMap<GlobalVariable, MpData> gv2md = new HashMap<>();
    public CodeGen(Module irModule, ArrayList<MpReg> mipsPhyRegs) {
        this.irModule = irModule;
        this.mipsModule = new MpModule();
        this.mipsPhyRegs = mipsPhyRegs;
    }
    public MpModule getMipsModule() { return this.mipsModule; }
    public void genModule() {
        // 生成data代码段
        LinkedList<GlobalVariable> globalVariables = irModule.getGlobalVariables();
        for (GlobalVariable globalVariable : globalVariables) {
            genGlobalVariable(globalVariable);
        }

        // 生成函数
        LinkedList<Function> functions = irModule.getFunctions();
        /* 把main函数放到text开头生成，这样的话text开头就不需要生成先跳转到main的代码（main的return 0 改为syscall） */
        Function main = irModule.getFunction("main");
        functions.remove(main);
        functions.addFirst(main);
        /* 先建函数和基本块，再生成代码 */
        for (Function function : functions) {
            if (function.isBuiltin()) continue;
            MpFunction mipsFunction = new MpFunction(function.getMipsName(), function.getArguments().size());
            mipsModule.addMpFunction(mipsFunction);
            f2mf.put(function, mipsFunction);
            MyLinkedList<BasicBlock> basicBlocks = function.getBlocks();
            for (BasicBlock basicBlock : basicBlocks) {
                MpBlock mipsBlock = new MpBlock(basicBlock.getMipsName(), mipsFunction);
                bb2mb.put(basicBlock, mipsBlock);
            }
            // 建立控制流图，为活动变量分析做准备
            for (BasicBlock basicBlock : basicBlocks) {
                MpBlock mipsBlock = bb2mb.get(basicBlock);
                HashSet<BasicBlock> precBBs = basicBlock.getPrecBBs();
                for (BasicBlock precBB : precBBs) {
                    MpBlock precMB = bb2mb.get(precBB);
                    mipsBlock.addPrecMB(precMB);
                    precMB.addSuccMB(mipsBlock);
                }
            }
        }
        for (Function function : functions) {
            if (!function.isBuiltin()) {
                curIF = function;
                curMF = f2mf.get(curIF);
                genFunction();
            }
        }
    }
    /*
     * 处理全局变量
     * 根据ir的Constant和Type处理出mipsData的信息
     */
    private void genGlobalVariable(GlobalVariable globalVariable) {
        MpData mipsData;
        Constant initializer = globalVariable.getInitializer();
        Type type = globalVariable.getType();
        if (globalVariable.isString())
            mipsData = new MpData(globalVariable.getMipsName(), ((ConstantStr)initializer).getVal());
        else if (initializer instanceof ConstantArray && ((ConstantArray)initializer).getVals().isEmpty())
            mipsData = new MpData(globalVariable.getMipsName(), type.size());
        else if (initializer instanceof ConstantInt)
            mipsData = new MpData(globalVariable.getMipsName(), new ArrayList<>(){{add(((ConstantInt)initializer).getVal());}});
        else
            mipsData = new MpData(globalVariable.getMipsName(), ((ConstantArray)initializer).getBases());
        mipsModule.addMpData(mipsData);
        gv2md.put(globalVariable, mipsData);
    }
    private boolean hasCallees;
    private void genFunction() {
        MyLinkedList<BasicBlock> basicBlocks = curIF.getBlocks();
        hasCallees = !curIF.getCallees().isEmpty();
        int offset = hasCallees ? 4 : 0; // $ra
        /* step1. 计算函数所需栈空间 */
        for (BasicBlock basicBlock : basicBlocks) {
            MyLinkedList<Instr> instrs = basicBlock.getInstrs();
            for (Instr instr : instrs) {
                if (instr instanceof Alloca) {
                    offset += instr.getType().size();
                    genAllocaInstr((Alloca) instr);
                }
            }
        }
        curMF.setStackSize(offset);
        /* step2. 函数开栈空间 */
        MpBlock tempMB =bb2mb.get(basicBlocks.getHead());
        /* step3.在函数开始时先将$ra保存在栈中 优化，若函数内没有jal指令，即没有调用其他函数，可以不用保存$ra */
        if (!curIF.isMain() && hasCallees) {
            MpStore mpStore = new MpStore(tempMB, mipsPhyRegs.get(29), mipsPhyRegs.get(27), new MpImm(0));
            tempMB.addMpInstr(mpStore);
        }
        /* step4. 获取函数形参 */
        // 取出参数
        int argSize = curIF.getArguments().size();
        for (int i = 0; i < argSize; i++) {
            if (i < 4) {
                MpReg src = mipsPhyRegs.get(4 + i);
                MpReg dst = new MpReg();
                MpMove mipsMove = new MpMove(tempMB, dst, src);
                tempMB.addMpInstr(mipsMove);
                val2opd.put(curIF.getArguments().get(i), dst);
            } else {
                MpReg dst = new MpReg();
                MpLoad mipsLoad = new MpLoad(tempMB, dst, mipsPhyRegs.get(27), new MpImm(i * 4));
                mipsLoad.setSPreference();
                tempMB.addMpInstr(mipsLoad);
                val2opd.put(curIF.getArguments().get(i), dst);
            }
        }
        curMFOffset = 4;
        /* step6.生成mips指令 */
        for (BasicBlock basicBlock : basicBlocks) {
            curIB = basicBlock;
            curMB = bb2mb.get(curIB);
            genBlock();
        }
        /* step7. 处理 parallel copies */
        if(Config.isLLVMopt)
            handleParallelCopies();
    }
    private void handleParallelCopies() {
        MyLinkedList<BasicBlock> basicBlocks = curIF.getBlocks();
        for (BasicBlock basicBlock : basicBlocks) {
            if (basicBlock.getMipsName().equals("main_b16")) {
                int x = 1;
            }
            PCs pcs = basicBlock.getPcs();
            if (pcs == null) continue;
            curMB = bb2mb.get(basicBlock);
            MpInstr mipsTerminator = curMB.getLastMpInstr();
            ArrayList<PCs.ParallelCopy> parallelCopies = pcs.getParallelCopies();
            for (int i = parallelCopies.size() - 1; i >= 0; i--) {
                PCs.ParallelCopy parallelCopy = parallelCopies.get(i);
                MpOpd src = genOperand(parallelCopy.src);
                MpOpd dst = genOperand(parallelCopy.dst);
                if (src instanceof MpImm)
                    mipsTerminator.insertBefore(new MpLoadImm(curMB, (MpReg) dst, (MpImm) src));
                else
                    mipsTerminator.insertBefore(new MpMove(curMB, (MpReg) dst, (MpReg) src));
            }
        }
    }
    private void genBlock() {
        MyLinkedList<Instr> instrs = curIB.getInstrs();
        for (Instr instr : instrs) {
            genInstr(instr);
        }
    }
    private void genInstr(Instr instr) {
        switch (instr.getValueTy()) {
            case load -> genLoadInstr((Load) instr);
            case store -> genStoreInstr((Store) instr);
            case br -> genBrInstr((Br) instr);
            case ret -> genRetInstr((Ret) instr);
            case call -> genCallInstr((Call) instr);
            case icmp -> genIcmpInstr((Icmp) instr);
            case add -> genAddInstr((Alu) instr);
            case sub -> genSubInstr((Alu) instr);
            case mul -> genMulInstr((Alu) instr);
            case sdiv -> genSdivInstr((Alu) instr);
            case srem -> genSremInstr((Alu) instr);
            case and -> genAndInstr((Alu) instr);
            case or -> genOrInstr((Alu) instr);
            case phi -> genPhiInstr((Phi) instr);
            case getelementptr -> genGepInstr((GetElementPtr) instr);
            case zext -> genZextInstr((Zext) instr);
            case trunc -> genTruncInstr((Trunc) instr);
            default -> {}
        }
    }

    private void genAllocaInstr(Alloca instr) {
        MpStackOffset offset = new MpStackOffset(mipsPhyRegs.get(27), new MpImm(curMFOffset));
        val2opd.put(instr, offset);
        curMFOffset += instr.getType().size();
    }
    private void genLoadInstr(Load instr) {
        Value irPtr = instr.getOperand(0);
        MpReg dst = (MpReg) genOperand(instr);
        if (irPtr instanceof GlobalVariable) {
            curMB.addMpInstr(new MpLoad(curMB, dst, gv2md.get(irPtr), null, null));
        } else {
            MpOpd mipsPtr = val2opd.get(irPtr);
            MpStackOffset mipsSO = (MpStackOffset) mipsPtr;
            curMB.addMpInstr(new MpLoad(curMB, dst, mipsSO.getData(), mipsSO.getBase(), mipsSO.getOffset()));
        }
    }
    private void genStoreInstr(Store instr) {
        Value irPtr = instr.getPointer();
        Value irVal = instr.getValue();
        MpOpd val = genOperand(irVal);
        if (val instanceof MpImm) {
            MpReg tmp = new MpReg();
            curMB.addMpInstr(new MpLoadImm(curMB, tmp, (MpImm) val));
            val = tmp;
        }
        if (irPtr instanceof GlobalVariable) {
            curMB.addMpInstr(new MpStore(curMB, (MpReg) val, gv2md.get(irPtr), null, null));
        } else {
            MpOpd mipsPtr = val2opd.get(irPtr);
            MpStackOffset mipsSO = (MpStackOffset) mipsPtr;
            curMB.addMpInstr(new MpStore(curMB, (MpReg) val, mipsSO.getData(), mipsSO.getBase(), mipsSO.getOffset()));
        }
    }
    private void genBrInstr(Br instr) {
        if (!instr.isCondBr()) {
            BasicBlock bb = (BasicBlock) instr.getOperand(0);
            MpBlock mb = bb2mb.get(bb);
            curMB.addMpInstr(new MpJump(curMB, mb.getLabel()));
        } else { // irCond = icmp
           Icmp irCond = (Icmp) instr.getOperand(0);
           MpOpd mipsCond = genOperand(irCond);
           MpBlock trueBB = bb2mb.get(instr.getOperand(1));
           MpBlock falseBB = bb2mb.get(instr.getOperand(2));
           if (mipsCond instanceof MpImm) {
               if (((MpImm)mipsCond).getVal() > 0)
                   curMB.addMpInstr(new MpJump(curMB, trueBB.getLabel()));
               else
                   curMB.addMpInstr(new MpJump(curMB, falseBB.getLabel()));
           } else {
               MpCmp mipsCmp = (MpCmp) curMB.getLastMpInstr();
               curMB.removeInstr(mipsCmp);
               switch (mipsCmp.getInstrType()) {
                   case seq -> curMB.addMpInstr(new MpBranch(MpInstr.MipsInstrType.beq, curMB, mipsCmp.getSrc1Reg(), mipsCmp.getSrc2(), trueBB.getLabel()));
                   case sne -> curMB.addMpInstr(new MpBranch(MpInstr.MipsInstrType.bne, curMB, mipsCmp.getSrc1Reg(), mipsCmp.getSrc2(), trueBB.getLabel()));
                   case slt, slti -> curMB.addMpInstr(new MpBranch(MpInstr.MipsInstrType.blt, curMB, mipsCmp.getSrc1Reg(), mipsCmp.getSrc2(), trueBB.getLabel()));
                   case sle -> curMB.addMpInstr(new MpBranch(MpInstr.MipsInstrType.ble, curMB, mipsCmp.getSrc1Reg(), mipsCmp.getSrc2(), trueBB.getLabel()));
                   case sgt -> curMB.addMpInstr(new MpBranch(MpInstr.MipsInstrType.bgt, curMB, mipsCmp.getSrc1Reg(), mipsCmp.getSrc2(), trueBB.getLabel()));
                   case sge -> curMB.addMpInstr(new MpBranch(MpInstr.MipsInstrType.bge, curMB, mipsCmp.getSrc1Reg(), mipsCmp.getSrc2(), trueBB.getLabel()));
                   default -> {}
               }
               curMB.addMpInstr(new MpJump(curMB, falseBB.getLabel()));
           }
        }
    }
    /*
     * 生成ret指令
     * 1. 有返回值，写入$v0
     * 2. 无返回值，直接跳转
     */
    private void genRetInstr(Ret instr) {
        if (curIF.isMain()) {
            curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), new MpImm(10)));
            curMB.addMpInstr(new MpSyscall(curMB));
            return;
        }
        if (instr.hasReturnValue()) {
            MpOpd ret = genOperand(instr.getOperand(0));
            if (ret instanceof MpImm)
                curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), (MpImm) ret));
            else
                curMB.addMpInstr(new MpMove(curMB, mipsPhyRegs.get(2), (MpReg) ret));
        }
        if (hasCallees)
            curMB.addMpInstr(new MpLoad(curMB, mipsPhyRegs.get(29), mipsPhyRegs.get(27), new MpImm(0)));
        curMB.addMpInstr(new MpJump(curMB, mipsPhyRegs.get(29)));
    }
    /*
     * 生成操作数：考虑寄存器和立即数两种类型
     */
    public MpOpd genOperand(Value irVal) {
        if (irVal instanceof ConstantInt)
            return new MpImm(((ConstantInt)irVal).getVal());
        MpOpd dst = val2opd.get(irVal);
        if (null == dst) {
            dst = new MpReg();
            val2opd.put(irVal, dst);
        }
        if (dst instanceof MpStackOffset) {
            MpStackOffset so = (MpStackOffset) dst;
            MpReg tmp = new MpReg();
            curMB.addMpInstr(new MpLoad(curMB, tmp, so.getData(), so.getBase(), so.getOffset()));
            return tmp;
        } else return dst;

    }
    private void genBuiltinCall(Call instr) {
        Function irFunc = (Function) instr.getOperand(0);
        for (int i = 1; i < instr.operandsSize(); i++) {
            Value irArg = instr.getOperand(i);
            MpReg dst = mipsPhyRegs.get(3 + i);
            if (irArg instanceof ConstantInt)
                curMB.addMpInstr(new MpLoadImm(curMB, dst, new MpImm(((ConstantInt) irArg).getVal())));
            else {
                MpOpd arg = val2opd.get(irArg);
                if (arg == null) {
                    arg = new MpReg();
                    val2opd.put(irArg, arg);
                }
                if (arg instanceof MpStackOffset) {
                    MpStackOffset so = (MpStackOffset) arg;
                    if (irArg.getType().isIntegerTy())
                        curMB.addMpInstr(new MpLoad(curMB, dst, so.getData(), so.getBase(), so.getOffset()));
                    else {
                        curMB.addMpInstr(new MpLoadAddr(curMB, dst, so.getData(), so.getBase(), so.getOffset()));
                    }
                } else if (arg instanceof MpImm)
                    curMB.addMpInstr(new MpLoadImm(curMB, dst, (MpImm) arg));
                else
                    curMB.addMpInstr(new MpMove(curMB, mipsPhyRegs.get(3 + i), (MpReg) arg));
            }
        }
        switch (irFunc.getMipsName()) {
            case "getint" -> curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), new MpImm(5)));
            case "putint" -> curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), new MpImm(1)));
            case "putstr" -> curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), new MpImm(4)));
            case "putch" -> curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), new MpImm(11)));
            default -> {}
        }
        curMB.addMpInstr(new MpSyscall(curMB));
        if (instr.getType().isIntegerTy()) {
            MpReg dst = (MpReg) genOperand(instr);
            curMB.addMpInstr(new MpMove(curMB, dst, mipsPhyRegs.get(2)));
        }
    }
    private void genCallInstr(Call instr) {
        Function irFunc = (Function) instr.getOperand(0);
        if (irFunc.isBuiltin()) {
            genBuiltinCall(instr);
            return;
        }
        // step1.腾出寄存器
        int curStackSize = - (instr.operandsSize() - 1) * 4 -52;
        for (int i = 1; i < instr.operandsSize(); i++) {
            Value irArg = instr.getOperand(i);
            if (i <= 4) {
                MpReg dst = mipsPhyRegs.get(3 + i);
                if (irArg instanceof ConstantInt)
                    curMB.addMpInstr(new MpLoadImm(curMB, dst, new MpImm(((ConstantInt) irArg).getVal())));
                else {
                    MpOpd arg = val2opd.get(irArg);
                    if (arg instanceof MpStackOffset) {
                        MpStackOffset so = (MpStackOffset) arg;
                        if (irArg.getType().isIntegerTy())
                            curMB.addMpInstr(new MpLoad(curMB, dst, so.getData(), so.getBase(), so.getOffset()));
                        else
                            curMB.addMpInstr(new MpLoadAddr(curMB, dst, so.getData(), so.getBase(), so.getOffset()));
                    } else if (arg instanceof MpImm)
                        curMB.addMpInstr(new MpLoadImm(curMB, dst, (MpImm) arg));
                    else
                        curMB.addMpInstr(new MpMove(curMB, mipsPhyRegs.get(3 + i), (MpReg) arg));
                }
            } else {
                if (irArg instanceof ConstantInt) {
                    curMB.addMpInstr(new MpLoadImm(curMB, mipsPhyRegs.get(2), new MpImm(((ConstantInt) irArg).getVal())));
                    curMB.addMpInstr(new MpStore(curMB, mipsPhyRegs.get(2), mipsPhyRegs.get(27), new MpImm(curStackSize + (i-1)*4)));
                }
                else {
                    MpReg dst = new MpReg();
                    MpOpd arg = val2opd.get(irArg);
                    if (arg instanceof MpStackOffset) {
                        MpStackOffset so = (MpStackOffset) arg;
                        if (irArg.getType().isIntegerTy()) {
                            curMB.addMpInstr(new MpLoad(curMB, dst, so.getData(), so.getBase(), so.getOffset()));
                            curMB.addMpInstr(new MpStore(curMB, dst, mipsPhyRegs.get(27), new MpImm(curStackSize + (i-1)*4)));
                        } else if (null == so.getData() && null == so.getOffset()) {
                            curMB.addMpInstr(new MpStore(curMB, so.getBase(), mipsPhyRegs.get(27), new MpImm(curStackSize + (i-1)*4)));
                        } else {
                            curMB.addMpInstr(new MpLoadAddr(curMB, dst, so.getData(), so.getBase(), so.getOffset()));
                            curMB.addMpInstr(new MpStore(curMB, dst, mipsPhyRegs.get(27), new MpImm(curStackSize + (i-1)*4)));
                        }
                    } else
                        curMB.addMpInstr(new MpStore(curMB, (MpReg) arg, mipsPhyRegs.get(27), new MpImm(curStackSize + (i-1)*4)));
                }
            }
        }
        MpFunction callee = f2mf.get(irFunc);
        curMB.addMpInstr(new MpJump(curMB, callee.getLabel(), instr.operandsSize() - 1));
        if (instr.getType().isIntegerTy()) {
            MpReg dst = (MpReg) genOperand(instr);
            curMB.addMpInstr(new MpMove(curMB, dst, mipsPhyRegs.get(2)));
        }
    }
    private void genIcmpInstr(Icmp instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm & rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            int val;
            switch (instr.getOp()) {
                case eq -> val = lhsVal == rhsVal ? 1 : 0;
                case ne -> val = lhsVal != rhsVal ? 1 : 0;
                case sgt -> val = lhsVal > rhsVal ? 1 : 0;
                case sge -> val = lhsVal >= rhsVal ? 1 : 0;
                case slt -> val = lhsVal < rhsVal ? 1 : 0;
                case sle -> val = lhsVal <= rhsVal ? 1 : 0;
                default -> throw new RuntimeException("invalid icmp op");
            }
            val2opd.put(instr, new MpImm(val));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            switch (instr.getOp()) {
                case eq -> {
                    if (lhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.seq, curMB, dst, (MpReg) rhs, lhs));
                    else
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.seq, curMB, dst, (MpReg) lhs, rhs));
                }
                case ne -> {
                    if (lhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sne, curMB, dst, (MpReg) rhs, lhs));
                    else
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sne, curMB, dst, (MpReg) lhs, rhs));
                }
                case slt -> {
                    if (lhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sgt, curMB, dst, (MpReg) rhs, lhs));
                    else if (rhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.slti, curMB, dst, (MpReg) lhs, rhs));
                    else
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.slt, curMB, dst, (MpReg) lhs, rhs));
                }
                case sle -> {
                    if (lhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sge, curMB, dst, (MpReg) rhs, lhs));
                    else
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sle, curMB, dst, (MpReg) lhs, rhs));
                }
                case sgt -> {
                    if (lhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.slti, curMB, dst, (MpReg) rhs, lhs));
                    else
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sgt, curMB, dst, (MpReg) lhs, rhs));
                }
                case sge -> {
                    if (lhs instanceof MpImm)
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sle, curMB, dst, (MpReg) rhs, lhs));
                    else
                        curMB.addMpInstr(new MpCmp(MpInstr.MipsInstrType.sge, curMB, dst, (MpReg) lhs, rhs));
                }
            }
        }
    }
    /*
     * 生成add指令
     * 1.若两个操作数都是常数，直接计算结果
     * 2.若一个操作数是常数，另一个是变量，生成addiu指令
     * 3.若两个操作数都是变量，生成addu指令
     */
    private void genAddInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal + rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) rhs, (MpImm)lhs));
            } else if (rhs instanceof MpImm) {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) lhs, (MpImm) rhs));
            } else {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, dst, (MpReg) lhs, (MpReg) rhs));
            }
        }
    }
    /*
     * 生成sub指令
     * 同加法
     */
    private void genSubInstr(Alu instr) {
        if (instr.getName().equals("%i484")) {
            int x = 1;
        }
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal - rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                curMB.addMpInstr(new MpLoadImm(curMB, dst, (MpImm)lhs));
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, dst, (MpReg) rhs));
            } else if (rhs instanceof MpImm) {
                MpImm imm = new MpImm(-((MpImm)rhs).getVal());
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) lhs, imm));
            } else {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, (MpReg) lhs, (MpReg) rhs));
            }
        }
    }
    private void genMulInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal * rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                MpOpd tmp;
                tmp = lhs; lhs = rhs; rhs = tmp;
            }
            mulOptimize(dst, (MpReg) lhs, rhs);
        }
    }
    /*
     * 除法优化：
     * 1.若除数是常数
     *   a.若除数是2^k，n/2^k -> n >> k
     *   b.除以常量的优化
     * 2.若除数是变量
     *   调用div指令
     */
    private void genSdivInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal / rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                curMB.addMpInstr(new MpLoadImm(curMB, dst, (MpImm)lhs));
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, dst, (MpReg) rhs));
            } else if (rhs instanceof MpImm) {
                DivByConst divByConst = new DivByConst();
                divByConst.run((MpReg) lhs, ((MpImm) rhs).getVal(), dst, curMB);
            } else {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, (MpReg) lhs, (MpReg) rhs));
            }
        }
    }
    /*
     * 生成srem指令
     */
    private void genSremInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal % rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                curMB.addMpInstr(new MpLoadImm(curMB, dst, (MpImm)lhs));
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, (MpReg) rhs));
                curMB.addMpInstr(new MpMfhi(curMB, dst));
            } else if (rhs instanceof MpImm) {
                DivByConst divByConst = new DivByConst();
                divByConst.run((MpReg) lhs, ((MpImm) rhs).getVal(), dst, curMB);
                mulOptimize(dst, dst, rhs);
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, (MpReg) lhs, dst));
            } else {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.div, curMB, (MpReg) lhs, (MpReg) rhs));
                curMB.addMpInstr(new MpMfhi(curMB, dst));
            }
        }
    }
    /*
     * 生成and指令
     * 1.若两个操作数都是常数，直接计算结果
     * 2. 若一个操作数是常数，另一个是变量，生成andi指令
     * 3.其他情况，生成and指令
     */
    private void genAndInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal & rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.andi, curMB, dst, (MpReg) rhs, (MpImm)lhs));
            } else if (rhs instanceof MpImm) {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.andi, curMB, dst, (MpReg) lhs, (MpImm) rhs));
            } else {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.and, curMB, dst, (MpReg) lhs, (MpReg) rhs));
            }
        }
    }
    /*
     * 生成or指令
     * 1.若两个操作数都是常数，直接计算结果
     * 2.若一个操作数是常数，另一个是变量，生成ori指令
     * 3.其他情况，生成or指令
     */
    private void genOrInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal | rhsVal));
        } else {
            MpReg dst = (MpReg) genOperand(instr);
            if (lhs instanceof MpImm) {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.ori, curMB, dst, (MpReg) rhs, (MpImm)lhs));
            } else if (rhs instanceof MpImm) {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.ori, curMB, dst, (MpReg) lhs, (MpImm) rhs));
            } else {
                curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.or, curMB, dst, (MpReg) lhs, (MpReg) rhs));
            }
        }
    }
    private void genPhiInstr(Phi instr) {
        MpReg dst = (MpReg) genOperand(instr);
    }
    /*
     * gep
     * 全局变量、栈中变量、形参
     * ptr为一维数组、二维数组
     * gep结果为int，一维数组指针，二维数组指针
     */
    private void genGVGepInstr(GetElementPtr instr) {
        Value irPtr = instr.getOperand(0);
        GlobalVariable gv = (GlobalVariable) irPtr;
        MpImm offset = new MpImm(0);
        MpReg base = null;
        MpData data = gv2md.get(gv);
        ArrayList<Integer> dimSizes;
        Type gvType = ((PointerType)gv.getType()).getReferencedType();
        if (gvType.isIntegerTy()) {
            dimSizes = new ArrayList<>();
            dimSizes.add(4);
        } else
            dimSizes = ((ArrayType)gvType).getDimSizes();
        /* 计算base和offset */
        for (int i = 1; i < instr.operandsSize(); i++) {
            Value irIdx = instr.getOperand(i);
            if (irIdx instanceof ConstantInt)
                offset.addVal(((ConstantInt) irIdx).getVal() * dimSizes.get(i-1));
            else {
                MpOpd idx =  genOperand(irIdx);
                if (idx instanceof MpImm) {
                    offset.addVal(((MpImm) idx).getVal() * dimSizes.get(i-1));
                } else {
                    if (null == base) {
                        base = new MpReg();
                        mulOptimize(base, (MpReg) idx, new MpImm(dimSizes.get(i - 1)));
                    } else {
                        MpReg preBase = base;
                        base = new MpReg();
                        mulOptimize(base, (MpReg) idx, new MpImm(dimSizes.get(i - 1)));
                        curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, base, preBase, base));
                    }
                }
            }
        }
        MpStackOffset mipsSO = new MpStackOffset(data, base, offset);
        val2opd.put(instr, mipsSO);
    }
    private void genGepInstr(GetElementPtr instr) {
        MpData data = null;
        MpImm offset;
        MpReg base;
        MpReg preBase = null;
        Value irPtr = instr.getOperand(0);
        /* step1. 预处理base 和 offset */
        if (irPtr instanceof GlobalVariable) {
            genGVGepInstr(instr);
            return;
        } else {
            MpOpd ptr = val2opd.get(irPtr);
            if (ptr instanceof MpStackOffset) {
                MpStackOffset so = (MpStackOffset) ptr;
                data = so.getData();
                base = so.getBase();
                offset = so.getOffset().clone();
            }
            else {
                base = ((MpReg) ptr);
                offset = new MpImm(0);
            }
        }

        /* step2. 归一化dims*/
        ArrayList<Integer> dimSizes;
        Type irPtrType = ((PointerType)irPtr.getType()).getReferencedType();
        if (irPtrType.isIntegerTy()) {
            dimSizes = new ArrayList<>();
            dimSizes.add(4);
        } else
            dimSizes = ((ArrayType)irPtrType).getDimSizes();
        /* 计算base和offset */
        for (int i = 1; i < instr.operandsSize(); i++) {
            Value irIdx = instr.getOperand(i);
            if (irIdx instanceof ConstantInt)
                offset.addVal(((ConstantInt) irIdx).getVal() * dimSizes.get(i-1));
            else {
                MpOpd idx =  genOperand(irIdx);
                if (idx instanceof MpImm) {
                    offset.addVal(((MpImm) idx).getVal() * dimSizes.get(i-1));
                } else {
                    if (null == base) {
                        base = new MpReg();
                        mulOptimize(base, (MpReg) idx, new MpImm(dimSizes.get(i-1)));
                    } else {
                        preBase = base;
                        base = new MpReg();
                        mulOptimize(base, (MpReg) idx, new MpImm(dimSizes.get(i - 1)));
                        curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, base, preBase, base));
                    }
                }
            }
        }
        MpStackOffset mipsSO = new MpStackOffset(data, base, offset);
        val2opd.put(instr, mipsSO);
    }
    private void genZextInstr(Zext instr) {
        MpOpd src = genOperand(instr.getOperand(0));
        val2opd.put(instr, src);
    }
    private void genTruncInstr(Trunc instr) {
        MpOpd src = genOperand(instr.getOperand(0));
        val2opd.put(instr, src);
    }
    /*
     * 生成mul指令
     * 1.若两个操作数都是常数，直接计算结果
     * 2.若一个操作数是常数，另一个是变量，判断常数是否是2的倍数，若是，生成sll指令
     * 3.其他情况，生成mul指令
     * 若能转化成两条sll指令，可以优化
     */
    private void mulOptimize(MpReg dst, MpReg lhs, MpOpd rhs) {
        if (rhs instanceof MpImm) {
            int rhsVal = ((MpImm)rhs).getVal();
            if (rhsVal == 0) {
                curMB.addMpInstr(new MpLoadImm(curMB, dst, new MpImm(0)));
            } else if (rhsVal == 1) {
                if (lhs != dst)
                    curMB.addMpInstr(new MpMove(curMB, dst, lhs));
            } else if ((rhsVal & (rhsVal - 1)) == 0) {
                curMB.addMpInstr(new MpShift(MpInstr.MipsInstrType.sll, curMB, dst, lhs, new MpImm(Integer.numberOfTrailingZeros(rhsVal))));
            } else {
                int cnt = 0;
                int tmp = rhsVal;
                int now = 0;
                while (tmp > 0) {
                    if ((tmp & 1) == 1)
                        cnt++;
                    now++;
                    tmp >>= 1;
                }
                if (cnt == 2) {
                    tmp = rhsVal;
                    boolean isFirst = true;
                    MpReg lhsReg = lhs;
                    if (lhs == dst) {
                        lhsReg = new MpReg();
                        curMB.addMpInstr(new MpMove(curMB, lhsReg, lhs));
                    }
                    while (now >= 0) {
                        now--;
                        if ((tmp & (1 << now)) != 0) {
                            if (isFirst) {
                                curMB.addMpInstr(new MpShift(MpInstr.MipsInstrType.sll, curMB, dst, lhsReg, new MpImm(now)));
                                isFirst = false;
                            } else {
                                if (now == 0)
                                    curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, dst, dst, lhsReg));
                                else {
                                    curMB.addMpInstr(new MpShift(MpInstr.MipsInstrType.sll, curMB, mipsPhyRegs.get(2), lhsReg, new MpImm(now)));
                                    curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.addu, curMB, dst, dst, mipsPhyRegs.get(2)));
                                }
                            }
                        }
                    }
                } else
                    curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.mul, curMB, dst, lhs, (MpImm) rhs));
            }
        } else {
            curMB.addMpInstr(new MpAlu(MpInstr.MipsInstrType.mul, curMB, dst, lhs, (MpReg) rhs));
        }
    }
}
