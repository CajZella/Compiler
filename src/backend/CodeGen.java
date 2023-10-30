package backend;

import backend.lir.MpBlock;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpJump;
import backend.lir.mipsInstr.MpLoadImm;
import backend.lir.mipsInstr.MpSyscall;
import backend.lir.mipsOperand.MpData;
import backend.lir.MpFunction;
import backend.lir.MpModule;
import backend.lir.mipsOperand.MpImm;
import backend.lir.mipsOperand.MpOpd;
import backend.lir.mipsOperand.MpReg;
import ir.BasicBlock;
import ir.Function;
import ir.GlobalVariable;
import ir.Module;
import ir.Value;
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
import ir.types.PointerType;
import util.MyLinkedList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

// 不涉及栈的管理，这需要在寄存器分配后，在这里还是用虚拟寄存器
public class CodeGen {
    private Module module;
    private MpModule mipsModule;
//    private MpFunction curMF = null;
//    private MpBlock curMB = null;
    // 映射
    private HashMap<Function, MpFunction> f2mf = new HashMap<>();
    private HashMap<BasicBlock, MpBlock> bb2mb = new HashMap<>();
    private HashMap<Value, MpOpd> val2opd = new HashMap<>();
    private HashMap<GlobalVariable, MpData> gv2md = new HashMap<>();
    public CodeGen(Module module) {
        this.module = module;
        this.mipsModule = new MpModule();
    }
    public MpModule getMipsModule() { return this.mipsModule; }
    public void genModule() {
        LinkedList<GlobalVariable> globalVariables = module.getGlobalVariables();
        for (GlobalVariable globalVariable : globalVariables) {
            genGlobalVariable(globalVariable);
        }

        LinkedList<Function> functions = module.getFunctions();
        Function main = module.getFunction("main");
        functions.remove(main);
        functions.addFirst(main);
        for (Function function : functions) {
            MpFunction mipsFunction = new MpFunction(function.getMipsName());
            mipsModule.addMpFunction(mipsFunction);
            f2mf.put(function, mipsFunction);
            MyLinkedList<BasicBlock> basicBlocks = function.getBlocks();
            Iterator<BasicBlock> iterator = basicBlocks.iterator();
            while(iterator.hasNext()) {
                BasicBlock basicBlock = iterator.next();
                MpBlock mipsBlock = new MpBlock(basicBlock.getMipsName(), mipsFunction);
                bb2mb.put(basicBlock, mipsBlock);
            }
        }
        for (Function function : functions) {
            genFunction(function);
        }
    }
    private void genGlobalVariable(GlobalVariable globalVariable) {
        MpData mipsData = new MpData(globalVariable.getMipsName(), globalVariable.getInitializer(), ((PointerType)globalVariable.getType()).getReferencedType());
        mipsModule.addMpData(mipsData);
        gv2md.put(globalVariable, mipsData);
    }
    private void genFunction(Function function) {
        switch (function.getName()) {
            case "@getint":
                genGetint(function);
                break;
            case "@putint":
                genPutint(function);
                break;
            case "@putstr":
                genPutstr(function);
                break;
            case "@putch":
                genPutch(function);
                break;
            default:
                genNormalFunction(function);
                break;
        }
    }
    private void genGetint(Function function) {
        MpFunction mipsFunction = f2mf.get(function);
        MpBlock mipsBlock = new MpBlock(null, mipsFunction);
        new MpLoadImm(mipsBlock, new MpReg("$v0"), new MpImm(5));
        new MpSyscall(mipsBlock);
        new MpJump(MpInstr.MipsInstrType.jr, mipsBlock, new MpReg("$ra"));
    }
    private void genPutint(Function function) {
        MpFunction mipsFunction = f2mf.get(function);
        MpBlock mipsBlock = new MpBlock(null, mipsFunction);
        new MpLoadImm(mipsBlock, new MpReg("$v0"), new MpImm(1));
        new MpSyscall(mipsBlock);
        new MpJump(MpInstr.MipsInstrType.jr, mipsBlock, new MpReg("$ra"));
    }
    private void genPutstr(Function function) {
        MpFunction mipsFunction = f2mf.get(function);
        MpBlock mipsBlock = new MpBlock(null, mipsFunction);
        new MpLoadImm(mipsBlock, new MpReg("$v0"), new MpImm(4));
        new MpSyscall(mipsBlock);
        new MpJump(MpInstr.MipsInstrType.jr, mipsBlock, new MpReg("$ra"));
    }
    private void genPutch(Function function) {
        MpFunction mipsFunction = f2mf.get(function);
        MpBlock mipsBlock = new MpBlock(null, mipsFunction);
        new MpLoadImm(mipsBlock, new MpReg("$v0"), new MpImm(11));
        new MpSyscall(mipsBlock);
        new MpJump(MpInstr.MipsInstrType.jr, mipsBlock, new MpReg("$ra"));
    }
    private void genNormalFunction(Function function) {
        MyLinkedList<BasicBlock> basicBlocks = function.getBlocks();
        for (BasicBlock basicBlock : basicBlocks) {
            genBlock(basicBlock);
        }
    }
    private void genBlock(BasicBlock basicBlock) {
        MyLinkedList<Instr> instrs = basicBlock.getInstrs();
        for (Instr instr : instrs) {
            genInstr(instr, basicBlock);
        }
    }
    private void genInstr(Instr instr, BasicBlock basicBlock) {
        switch (instr.getValueTy()) {
            case alloca:
                genAllocaInstr((Alloca)instr, basicBlock);
                break;
            case load:
                genLoadInstr((Load)instr, basicBlock);
                break;
            case store:
                genStoreInstr((Store)instr, basicBlock);
                break;
            case br:
                genBrInstr((Br)instr, basicBlock);
                break;
            case ret:
                genRetInstr((Ret)instr, basicBlock);
                break;
            case call:
                genCallInstr((Call)instr, basicBlock);
                break;
            case icmp:
                genIcmpInstr((Icmp)instr, basicBlock);
                break;
            case add:
            case sub:
            case mul:
            case sdiv:
            case srem:
            case and:
            case or:
                genAluInstr((Alu)instr, basicBlock);
                break;
            case phi:
                genPhiInstr((Phi)instr, basicBlock);
                break;
            case zext:
                genZextInstr((Zext)instr, basicBlock);
                break;
            case trunc:
                genTruncInstr((Trunc)instr, basicBlock);
                break;
            case getelementptr:
                genGepInstr((GetElementPtr)instr, basicBlock);
                break;
            default:
                break;
        }
    }
    private void genAllocaInstr(Alloca instr, BasicBlock basicBlock) {

    }
    private void genLoadInstr(Load instr, BasicBlock basicBlock) {}
    private void genStoreInstr(Store instr, BasicBlock basicBlock) {}
    private void genBrInstr(Br instr, BasicBlock basicBlock) {}
    private void genRetInstr(Ret instr, BasicBlock basicBlock) {}
    private void genCallInstr(Call instr, BasicBlock basicBlock) {}
    private void genIcmpInstr(Icmp instr, BasicBlock basicBlock) {}
    private void genAluInstr(Alu instr, BasicBlock basicBlock) {}
    private void genPhiInstr(Phi instr, BasicBlock basicBlock) {}
    private void genZextInstr(Zext instr, BasicBlock basicBlock) {}
    private void genTruncInstr(Trunc instr, BasicBlock basicBlock) {}
    private void genGepInstr(GetElementPtr instr, BasicBlock basicBlock) {}
}
