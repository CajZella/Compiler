package pass;

import ir.Argument;
import ir.BasicBlock;
import ir.Function;
import ir.Value;
import ir.constants.Constant;
import ir.constants.ConstantArray;
import ir.constants.ConstantInt;
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
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.VoidType;

import java.util.ArrayList;
import java.util.HashMap;

public class Interpreter {
    private Instr callIni;
    private final Function function;
    private final HashMap<Value, Constant> val2cnt = new HashMap<>();
    private int result;
    private BasicBlock lastBlock = null;

    public Interpreter(ArrayList<Constant> args, Function function, Instr callIni) {
        for (int i = 0; i < args.size(); i++) {
            Argument argument = function.getArguments().get(i);
            val2cnt.put(argument, args.get(i));
        }
        this.function = function;
        this.callIni = callIni;
    }
    public int interpretFunc() {
        interpretBlock(function.getEntryBlock());
        return result;
    }
    private void interpretBlock(BasicBlock block) {
        for (Instr instr : block.getInstrs()) {
            interpretInstr(instr);
        }
    }
    private void interpretInstr(Instr instr) {
        switch (instr.getValueTy()) {
            case alloca -> interpretAlloca((Alloca) instr);
            case add, sub, mul, sdiv, srem, and, or -> interpretAlu((Alu) instr);
            case icmp -> interpretIcmp((Icmp) instr);
            case br -> interpretBr((Br) instr);
            case call -> interpretCall((Call) instr);
            case ret -> interpretRet((Ret) instr);
            case load -> interpretLoad((Load) instr);
            case store -> interpretStore((Store) instr);
            case getelementptr -> interpretGEP((GetElementPtr) instr);
            case phi -> interpretPhi((Phi) instr);
            case zext -> interpretZext((Zext) instr);
            case trunc -> interpretTrunc((Trunc) instr);
        }
    }
    private void interpretAlloca(Alloca alloca) {
        ArrayType arrayType = (ArrayType) ((PointerType) alloca.getType()).getReferencedType();
        ConstantArray array = new ConstantArray(arrayType, true);
        val2cnt.put(alloca, array);
    }
    private void interpretAlu(Alu alu) {
        int op1 = ((ConstantInt)getConstant(alu.getOperand(0))).getVal();
        int op2 = ((ConstantInt)getConstant(alu.getOperand(1))).getVal();
        switch (alu.getValueTy()) {
            case add -> result = op1 + op2;
            case sub -> result = op1 - op2;
            case mul -> result = op1 * op2;
            case sdiv -> result = op1 / op2;
            case srem -> result = op1 % op2;
            case and -> result = op1 & op2;
            case or -> result = op1 | op2;
        }
        val2cnt.put(alu, new ConstantInt(new IntegerType(32), result));
    }
    private void interpretIcmp(Icmp icmp) {
        int op1 = ((ConstantInt)getConstant(icmp.getOperand(0))).getVal();
        int op2 = ((ConstantInt)getConstant(icmp.getOperand(1))).getVal();
        switch (icmp.getOp()) {
            case eq -> result = op1 == op2 ? 1 : 0;
            case ne -> result = op1 != op2 ? 1 : 0;
            case sgt -> result = op1 > op2 ? 1 : 0;
            case sge -> result = op1 >= op2 ? 1 : 0;
            case slt -> result = op1 < op2 ? 1 : 0;
            case sle -> result = op1 <= op2 ? 1 : 0;
        }
        val2cnt.put(icmp, new ConstantInt(new IntegerType(1), result));
    }
    private void interpretBr(Br br) {
        lastBlock = br.getParent();
        if (br.isCondBr()) {
            int cond = ((ConstantInt)getConstant(br.getOperand(0))).getVal();
            if (cond == 1)
                interpretBlock(br.getTrueBB());
             else
                interpretBlock(br.getFalseBB());
        } else
            interpretBlock(br.getDestBB());
    }
    private void interpretCall(Call call) {
        Function callee = call.getCallee();
        if (callee.isBuiltin()) {
            if (callee.getName().equals("@putch")) {
                Call newCall = new Call(new VoidType(), callIni.getParent(), callee, call.getOperand(1));
                callIni.insertBefore(newCall);
                callIni = newCall;
            } else if (callee.getName().equals("@putstr")) {
                GetElementPtr gep = (GetElementPtr) call.getOperand(1);
                gep = new GetElementPtr((PointerType) gep.getType(), callIni.getParent(), gep.getOperands().toArray(new Value[gep.operandsSize()]));
                callIni.insertBefore(gep);
                Call newCall = new Call(new VoidType(), callIni.getParent(), callee, gep);
                callIni.insertBefore(newCall);
                callIni = newCall;
            } else {
                ConstantInt value = (ConstantInt) getConstant(call.getOperand(1));
                Call newCall = new Call(new VoidType(), callIni.getParent(), callee, value);
                callIni.insertBefore(newCall);
                callIni = newCall;
            }
            return;
        }
        ArrayList<Constant> args = new ArrayList<>();
        for (int i = 1; i < call.operandsSize(); i++) {
            Value arg = call.getOperand(i);
            args.add(getConstant(arg));
        }
        Interpreter interpreter = new Interpreter(args, callee, callIni);
        int ret = interpreter.interpretFunc();
        val2cnt.put(call, new ConstantInt(new IntegerType(32), ret));
    }
    private void interpretRet(Ret instr) {
        assert instr.hasReturnValue();
        result = ((ConstantInt) getConstant(instr.getOperand(0))).getVal();
    }
    private void interpretLoad(Load instr) {
        val2cnt.put(instr, getConstant(instr.getPointer()));
    }
    private void interpretStore(Store instr) {
        ConstantInt pointer = (ConstantInt) getConstant(instr.getPointer());
        ConstantInt value = (ConstantInt) getConstant(instr.getValue());
        pointer.setVal(value.getVal());
    }
    private void interpretGEP(GetElementPtr instr) {
        Constant pointer = getConstant(instr.getOperand(0));
        ArrayList<Integer> idxs = new ArrayList<>();
        for (int i = 1; i < instr.operandsSize(); i++) {
            idxs.add(((ConstantInt)getConstant(instr.getOperand(i))).getVal());
        }
        Constant result = pointer.getElement(idxs, ((PointerType) instr.getOperand(0).getType()).getReferencedType());
        val2cnt.put(instr, result);
    }
    private void interpretPhi(Phi instr) {
        for (int i = 0; i < instr.getPhiBBs().size(); i++) {
            BasicBlock phiBB = instr.getBlock(i);
            if (phiBB == lastBlock)
                val2cnt.put(instr, getConstant(instr.getOperand(i)));
        }
    }
    private void interpretZext(Zext instr) {
        val2cnt.put(instr, new ConstantInt(instr.getType(), ((ConstantInt)getConstant(instr.getOperand(0))).getVal()));
    }
    private void interpretTrunc(Trunc instr) {
        val2cnt.put(instr, new ConstantInt(instr.getType(), ((ConstantInt)getConstant(instr.getOperand(0))).getVal()));
    }
    private Constant getConstant(Value value) {
        if (value instanceof Constant)
            return (Constant) value;
        else
            return val2cnt.get(value);
    }
}
