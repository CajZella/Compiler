package backend;

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
import backend.lir.mipsOperand.MpPhyReg;
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
import util.MyLinkedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

// 不涉及栈的管理，这需要在寄存器分配后，在这里还是用虚拟寄存器
public class CodeGen {
    private Module irModule;
    private MpModule mipsModule;
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
     * mipsOperand包括 reg 和 argument, MpImm, MpStackOffset
     */
    private HashMap<Value, MpOpd> val2opd = new HashMap<>();
    /*
     * irGlobalVariable到mipsData的映射
     * 方便后续要用到globalVariable的指令
     */
    private HashMap<GlobalVariable, MpData> gv2md = new HashMap<>();
    public CodeGen(Module irModule) {
        this.irModule = irModule;
        this.mipsModule = new MpModule();
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
        /* 先建函数和基本开，再生成代码 */
        for (Function function : functions) {
            if (function.isBuiltin()) continue;
            MpFunction mipsFunction = new MpFunction(function.getMipsName());
            mipsModule.addMpFunction(mipsFunction);
            f2mf.put(function, mipsFunction);
            MyLinkedList<BasicBlock> basicBlocks = function.getBlocks();
            for (BasicBlock basicBlock : basicBlocks) {
                MpBlock mipsBlock = new MpBlock(basicBlock.getMipsName(), mipsFunction);
                bb2mb.put(basicBlock, mipsBlock);
            }
        }
        // todo: 中端建控制流图，生成phi，后端消除phi
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
    private void genFunction() {
        MyLinkedList<BasicBlock> basicBlocks = curIF.getBlocks();
        int offset = 4; // $ra
        for (BasicBlock basicBlock : basicBlocks) {
            MyLinkedList<Instr> instrs = basicBlock.getInstrs();
            for (Instr instr : instrs) {
                if (instr instanceof Alloca)
                    offset += instr.getType().size();
            }
        }
        curMF.setStackSize(offset);
        MpBlock tempMB =bb2mb.get(basicBlocks.getHead());
//        // 取出参数
//        for (int i = 0; i < curIF.getArguments().size(); i++) {
//            if (i < 4) {
//                MpReg src = new MpReg(MpPhyReg.getReg(3 + i));
//                MpReg dst = new MpReg();
//                new MpMove(tempMB, dst, src);
//                val2opd.put(curIF.getArguments().get(i), dst);
//            } else {
//                MpReg dst = new MpReg();
//                val2opd.put(curIF.getArguments().get(i), dst);
//                new MpLoad(tempMB, dst, new MpReg(MpPhyReg.$sp), new MpImm((i - 4) * 4));
//            }
//        }
        new MpAlu(MpInstr.MipsInstrType.addiu, tempMB, new MpReg(MpPhyReg.$sp), new MpReg(MpPhyReg.$sp), new MpImm(-offset));
        // 在函数开始时先将$ra保存在栈中 todo: 待优化，若函数内没有jal指令，即没有调用其他函数，可以不用保存$ra
        new MpStore(tempMB, new MpReg(MpPhyReg.$ra), new MpReg(MpPhyReg.$sp), new MpImm(0));
        curMFOffset = 4;
        for (BasicBlock basicBlock : basicBlocks) {
            curIB = basicBlock;
            curMB = bb2mb.get(curIB);
            genBlock();
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
            case alloca -> genAllocaInstr((Alloca) instr);
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
    private void genZextInstr(Zext instr) {
        MpReg src = (MpReg) val2opd.get(instr.getOperand(0));
        val2opd.put(instr, src);
    }
    private void genTruncInstr(Trunc instr) {
        MpReg src = (MpReg) val2opd.get(instr.getOperand(0));
        val2opd.put(instr, src);
    }
    /*
     *
     */
    private void genAllocaInstr(Alloca instr) {
        if (instr.getType().isIntegerTy()) {
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
        } else {
            MpStackOffset offset = new MpStackOffset(new MpReg(MpPhyReg.$sp), new MpImm(curMFOffset));
            val2opd.put(instr, offset);
        }
        curMFOffset += instr.getType().size();
    }
    private void genLoadInstr(Load instr) {
        Value irPtr = instr.getOperand(0);
        if (irPtr instanceof GetElementPtr) {
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            MpStackOffset mipsSO = (MpStackOffset) val2opd.get(irPtr);
            new MpLoad(curMB, dst, mipsSO.getBase(), mipsSO.getOffset());
        } else if (irPtr instanceof GlobalVariable) {
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            new MpLoad(curMB, dst, gv2md.get(irPtr));
        } else {
            val2opd.put(instr, val2opd.get(irPtr));
        }
    }
    private void genStoreInstr(Store instr) {
        Value irPtr = instr.getPointer();
        Value irVal = instr.getValue();
        MpOpd val = genOperand(irVal);
        if (val instanceof MpImm) {
            MpReg tmp = new MpReg();
            new MpLoadImm(curMB, tmp, (MpImm) val);
            val = tmp;
        }
        if (irPtr instanceof GetElementPtr) {
            MpStackOffset mipsSO = (MpStackOffset) val2opd.get(irPtr);
            new MpStore(curMB, (MpReg) val, mipsSO.getBase(), mipsSO.getOffset());
        }  else if (irPtr instanceof GlobalVariable) {
            new MpStore(curMB, (MpReg) val, gv2md.get(irPtr));
        }
    }
    private void genBrInstr(Br instr) {
        if (!instr.isCondBr()) {
            BasicBlock bb = (BasicBlock) instr.getOperand(0);
            MpBlock mb = bb2mb.get(bb);
            new MpJump(MpInstr.MipsInstrType.j, curMB, mb.getLabel());
        } else { // irCond = icmp
           Icmp irCond = (Icmp) instr.getOperand(0);
           MpOpd mipsCond = genOperand(irCond);
           MpBlock trueBB = bb2mb.get(instr.getOperand(1));
           MpBlock falseBB = bb2mb.get(instr.getOperand(2));
           if (mipsCond instanceof MpImm) {
               if (((MpImm)mipsCond).getVal() > 0)
                   new MpJump(MpInstr.MipsInstrType.j, curMB, trueBB.getLabel());
               else
                   new MpJump(MpInstr.MipsInstrType.j, curMB, falseBB.getLabel());
           } else {
               MpCmp mipsCmp = (MpCmp) curMB.getLastMpInstr();
               curMB.removeInstr(mipsCmp);
               switch (mipsCmp.getInstrType()) {
                   case seq -> new MpBranch(MpInstr.MipsInstrType.beq, curMB, mipsCmp.getSourceReg(), mipsCmp.getTarget(), trueBB.getLabel());
                   case sne -> new MpBranch(MpInstr.MipsInstrType.bne, curMB, mipsCmp.getSourceReg(), mipsCmp.getTarget(), trueBB.getLabel());
                   case slt -> new MpBranch(MpInstr.MipsInstrType.blt, curMB, mipsCmp.getSourceReg(), mipsCmp.getTarget(), trueBB.getLabel());
                   case sle -> new MpBranch(MpInstr.MipsInstrType.ble, curMB, mipsCmp.getSourceReg(), mipsCmp.getTarget(), trueBB.getLabel());
                   case sgt -> new MpBranch(MpInstr.MipsInstrType.bgt, curMB, mipsCmp.getSourceReg(), mipsCmp.getTarget(), trueBB.getLabel());
                   case sge -> new MpBranch(MpInstr.MipsInstrType.bge, curMB, mipsCmp.getSourceReg(), mipsCmp.getTarget(), trueBB.getLabel());
                   default -> {}
               }
               new MpJump(MpInstr.MipsInstrType.j, curMB, falseBB.getLabel());
           }
        }
    }
    /*
     * 生成ret指令
     * 1. 有返回值，写入$v0
     * 2. 无返回值，直接跳转
     */
    private void genRetInstr(Ret instr) {
        if (curIF.getMipsName().equals("main")) {
            new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), new MpImm(10));
            new MpSyscall(curMB);
            return;
        }
        new MpAlu(MpInstr.MipsInstrType.addiu, curMB, new MpReg(MpPhyReg.$sp), new MpReg(MpPhyReg.$sp), new MpImm(curMF.getStackSize()));
        if (instr.hasReturnValue()) {
            MpOpd ret = genOperand(instr.getOperand(0));
            if (ret instanceof MpImm)
                new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), (MpImm) ret);
            else
                new MpMove(curMB, new MpReg(MpPhyReg.$v0), (MpReg) ret);
        }
        new MpLoad(curMB, new MpReg(MpPhyReg.$ra), new MpReg(MpPhyReg.$sp), new MpImm(0));
        new MpJump(MpInstr.MipsInstrType.jr, curMB, new MpReg(MpPhyReg.$ra));
    }
    /*
     * 生成操作数：考虑寄存器和立即数两种类型
     */
    public MpOpd genOperand(Value irVal) {
        if (irVal instanceof ConstantInt)
            return new MpImm(((ConstantInt)irVal).getVal());
        MpOpd dst = val2opd.get(irVal);
        if (dst instanceof MpStackOffset) {
            MpReg tmp = new MpReg();
            new MpLoad(curMB, tmp, ((MpStackOffset) dst).getBase(), ((MpStackOffset) dst).getOffset());
            return tmp;
        } else return dst;

    }
    private void genCallInstr(Call instr) {
        Function irFunc = (Function) instr.getOperand(0);
        int offset = 0;
        for (int i = 1; i < instr.getNumOperands(); i++) {
            Value irArg = instr.getOperand(i);
            if (i <= 4) {
                MpReg dst = new MpReg(MpPhyReg.getReg(3 + i));
                if (irArg instanceof ConstantInt)
                    new MpLoadImm(curMB, dst, new MpImm(((ConstantInt) irArg).getVal()));
                else {
                    MpOpd arg = val2opd.get(irArg);
                    if (arg instanceof MpStackOffset) {
                        MpReg base = ((MpStackOffset) arg).getBase();
                        MpImm soOffset = ((MpStackOffset) arg).getOffset();
                        if (irArg.getType().isIntegerTy())
                            new MpLoad(curMB, dst, base, soOffset);
                        else if (soOffset.getVal() == 0)
                            new MpMove(curMB, dst, base);
                        else
                            new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, base, soOffset);
                    } else
                        new MpMove(curMB, new MpReg(MpPhyReg.getReg(3 + i)), (MpReg) arg);
                }
            } else {
                offset -= 4;
                if (irArg instanceof ConstantInt) {
                    new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), new MpImm(((ConstantInt) irArg).getVal()));
                    new MpStore(curMB, new MpReg(MpPhyReg.$v0), new MpReg(MpPhyReg.$sp), new MpImm(offset));
                }
                else {
                    MpReg dst = new MpReg();
                    MpOpd arg = val2opd.get(irArg);
                    if (arg instanceof MpStackOffset) {
                        MpReg base = ((MpStackOffset) arg).getBase();
                        MpImm soOffset = ((MpStackOffset) arg).getOffset();
                        if (irArg.getType().isIntegerTy()) {
                            new MpLoad(curMB, dst, base, soOffset);
                            new MpStore(curMB, dst, new MpReg(MpPhyReg.$sp), new MpImm(offset));
                        }
                        else if (soOffset.getVal() == 0)
                            new MpStore(curMB, base, new MpReg(MpPhyReg.$sp), new MpImm(offset));
                        else {
                            new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, base, soOffset);
                            new MpStore(curMB, dst, new MpReg(MpPhyReg.$sp), new MpImm(offset));
                        }
                    } else
                        new MpStore(curMB, (MpReg) arg, new MpReg(MpPhyReg.$sp), new MpImm(offset));
                }

            }
        }
        if (irFunc.isBuiltin()) {
            switch (irFunc.getMipsName()) {
                case "getint" -> new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), new MpImm(5));
                case "putint" -> new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), new MpImm(1));
                case "putstr" -> new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), new MpImm(4));
                case "putch" -> new MpLoadImm(curMB, new MpReg(MpPhyReg.$v0), new MpImm(11));
                default -> {}
            }
            new MpSyscall(curMB);
        } else {
            if (offset != 0)
                new MpAlu(MpInstr.MipsInstrType.addiu, curMB, new MpReg(MpPhyReg.$sp), new MpReg(MpPhyReg.$sp), new MpImm(offset));
            MpFunction callee = f2mf.get(irFunc);
            new MpJump(MpInstr.MipsInstrType.jal, curMB, callee.getLabel());
            if (offset != 0)
                new MpAlu(MpInstr.MipsInstrType.addiu, curMB, new MpReg(MpPhyReg.$sp), new MpReg(MpPhyReg.$sp), new MpImm(-offset));
        }
        if (instr.getType().isIntegerTy()) {
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            new MpMove(curMB, dst, new MpReg(MpPhyReg.$v0));
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
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            switch (instr.getOp()) {
                case eq -> {
                    if (lhs instanceof MpImm)
                        new MpCmp(MpInstr.MipsInstrType.seq, curMB, dst, (MpReg) rhs, lhs);
                    else
                        new MpCmp(MpInstr.MipsInstrType.seq, curMB, dst, (MpReg) lhs, rhs);
                }
                case ne -> {
                    if (lhs instanceof MpImm)
                        new MpCmp(MpInstr.MipsInstrType.sne, curMB, dst, (MpReg) rhs, lhs);
                    else
                        new MpCmp(MpInstr.MipsInstrType.sne, curMB, dst, (MpReg) lhs, rhs);
                }
                case slt -> {
                    if (lhs instanceof MpImm)
                        new MpCmp(MpInstr.MipsInstrType.sgt, curMB, dst, (MpReg) rhs, lhs);
                    else
                        new MpCmp(MpInstr.MipsInstrType.slt, curMB, dst, (MpReg) lhs, rhs);
                }
                case sle -> {
                    if (lhs instanceof MpImm)
                        new MpCmp(MpInstr.MipsInstrType.sge, curMB, dst, (MpReg) rhs, lhs);
                    else
                        new MpCmp(MpInstr.MipsInstrType.sle, curMB, dst, (MpReg) lhs, rhs);
                }
                case sgt -> {
                    if (lhs instanceof MpImm)
                        new MpCmp(MpInstr.MipsInstrType.slt, curMB, dst, (MpReg) rhs, lhs);
                    else
                        new MpCmp(MpInstr.MipsInstrType.sgt, curMB, dst, (MpReg) lhs, rhs);
                }
                case sge -> {
                    if (lhs instanceof MpImm)
                        new MpCmp(MpInstr.MipsInstrType.sle, curMB, dst, (MpReg) rhs, lhs);
                    else
                        new MpCmp(MpInstr.MipsInstrType.sge, curMB, dst, (MpReg) lhs, rhs);
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
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) rhs, (MpImm)lhs);
            } else if (rhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) lhs, (MpImm) rhs);
            } else {
                new MpAlu(MpInstr.MipsInstrType.addu, curMB, dst, (MpReg) lhs, (MpReg) rhs);
            }
        }
    }
    /*
     * 生成sub指令
     * 同加法
     */
    private void genSubInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal - rhsVal));
        } else {
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                ((MpImm)lhs).setVal(-((MpImm)lhs).getVal());
                new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) rhs, (MpImm)lhs);
            } else if (rhs instanceof MpImm) {
                ((MpImm)rhs).setVal(-((MpImm)rhs).getVal());
                new MpAlu(MpInstr.MipsInstrType.addiu, curMB, dst, (MpReg) lhs, (MpImm) rhs);
            } else {
                new MpAlu(MpInstr.MipsInstrType.subu, curMB, dst, (MpReg) lhs, (MpReg) rhs);
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
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                MpOpd tmp;
                tmp = lhs; lhs = rhs; rhs = tmp;
            }
            mulOptimize(dst, (MpReg) lhs, rhs);
        }
    }
    /* todo
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
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                new MpLoadImm(curMB, dst, (MpImm)lhs);
                new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, dst, (MpReg) rhs);
            } else if (rhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, (MpReg) lhs, (MpImm) rhs);
            } else {
                new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, (MpReg) lhs, (MpReg) rhs);
            }
        }
    }
    /*
     * 生成srem指令
     * todo: 除法优化->余数优化
     */
    private void genSremInstr(Alu instr) {
        MpOpd lhs = genOperand(instr.getOperand(0));
        MpOpd rhs = genOperand(instr.getOperand(1));
        if (lhs instanceof MpImm && rhs instanceof MpImm) {
            int lhsVal = ((MpImm)lhs).getVal();
            int rhsVal = ((MpImm)rhs).getVal();
            val2opd.put(instr, new MpImm(lhsVal % rhsVal));
        } else {
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                new MpLoadImm(curMB, dst, (MpImm)lhs);
                new MpAlu(MpInstr.MipsInstrType.div, curMB, dst, (MpReg) rhs);
                new MpMfhi(curMB, dst);
            } else if (rhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.div, curMB, (MpReg) lhs, (MpImm) rhs);
                new MpMfhi(curMB, dst);
            } else {
                new MpAlu(MpInstr.MipsInstrType.div, curMB, (MpReg) lhs, (MpReg) rhs);
                new MpMfhi(curMB, dst);
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
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.andi, curMB, dst, (MpReg) rhs, (MpImm)lhs);
            } else if (rhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.andi, curMB, dst, (MpReg) lhs, (MpImm) rhs);
            } else {
                new MpAlu(MpInstr.MipsInstrType.and, curMB, dst, (MpReg) lhs, (MpReg) rhs);
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
            MpReg dst = new MpReg();
            val2opd.put(instr, dst);
            if (lhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.ori, curMB, dst, (MpReg) rhs, (MpImm)lhs);
            } else if (rhs instanceof MpImm) {
                new MpAlu(MpInstr.MipsInstrType.ori, curMB, dst, (MpReg) lhs, (MpImm) rhs);
            } else {
                new MpAlu(MpInstr.MipsInstrType.or, curMB, dst, (MpReg) lhs, (MpReg) rhs);
            }
        }
    }
    private void genPhiInstr(Phi instr) {}
    /*
     * gep
     * 全局变量、栈中变量、形参
     * ptr为一维数组、二维数组
     * gep结果为int，一维数组指针，二维数组指针
     */
    private void genGepInstr(GetElementPtr instr) {
        MpImm offset;
        MpReg base;
        MpReg preBase = null;
        Value irPtr = instr.getOperand(0);
        /* step1. 预处理base 和 offset */
        if (irPtr instanceof GlobalVariable) {
            base = new MpReg(MpPhyReg.$v0);
            offset = new MpImm(0);
            new MpLoadAddr(curMB, base, gv2md.get(irPtr));
        } else {
            MpOpd ptr = val2opd.get(irPtr);
            if (ptr instanceof MpStackOffset) {
                base = ((MpStackOffset) ptr).getBase();
                offset = ((MpStackOffset) ptr).getOffset();
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
            dimSizes.add(0);
        } else
            dimSizes = ((ArrayType)irPtrType).getDimSizes();
        /* 计算base和offset */
        for (int i = 0; i < instr.getIdxs().size(); i++) {
            Value irIdx = instr.getIdxs().get(i);
            if (irIdx instanceof ConstantInt)
                offset.addVal(((ConstantInt) irIdx).getVal() * dimSizes.get(i));
            else {
                MpReg idx = (MpReg) genOperand(irIdx);
                if (null == preBase) {
                    preBase = base;
                    base = new MpReg();
                }
                mulOptimize(base, idx, new MpImm(dimSizes.get(i)));
                new MpAlu(MpInstr.MipsInstrType.addu, curMB, base, preBase, base);
            }
        }
        MpStackOffset mipsSO = new MpStackOffset(base, offset);
        val2opd.put(instr, mipsSO);
    }
    /*
     * 生成mul指令
     * 1.若两个操作数都是常数，直接计算结果
     * 2.若一个操作数是常数，另一个是变量，判断常数是否是2的倍数，若是，生成sll指令
     * 3.其他情况，生成mul指令
     * todo: 待优化 若能转化成两条sll指令，可以优化
     */
    private void mulOptimize(MpReg dst, MpReg lhs, MpOpd rhs) {
        if (rhs instanceof MpImm) {
            int rhsVal = ((MpImm)rhs).getVal();
            if ((rhsVal & (rhsVal - 1)) == 0) {
                new MpShift(MpInstr.MipsInstrType.sll, curMB, dst, lhs, new MpImm(Integer.numberOfTrailingZeros(rhsVal)));
            } else {
                new MpAlu(MpInstr.MipsInstrType.mul, curMB, dst, lhs, (MpImm) rhs);
            }
        } else {
            new MpAlu(MpInstr.MipsInstrType.mul, curMB, dst, lhs, (MpReg) rhs);
        }
    }
    private void divOptimize(MpReg dst, MpReg lhs, MpOpd rhs) {}
}