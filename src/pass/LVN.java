package pass;

import ir.BasicBlock;
import ir.Module;
import ir.Function;
import ir.Value;
import ir.constants.ConstantInt;
import ir.instrs.Alu;
import ir.instrs.Icmp;
import ir.instrs.Instr;
import ir.instrs.Trunc;
import ir.instrs.Zext;

import java.util.HashMap;


public class LVN {
    private Module module;
    public LVN(Module module) {
        this.module = module;
    }
    public void run() {
        for (Function function : module.getFunctions()) {
            while (!runFuncLVN(function));
        }
    }
    public boolean runFuncLVN(Function function) {
        boolean finished = true;
        for (BasicBlock block : function.getBlocks()) {
             finished &= runLVN(block);
        }
        return finished;
    }
    private boolean runLVN(BasicBlock block) {
        boolean finished = true;
        HashMap<Integer, Instr> map = new HashMap<>();
        for (Instr instr : block.getInstrs()) {
            if (instr instanceof Alu) {
                Value op1 = instr.getOperand(0);
                Value op2 = instr.getOperand(1);
                if (op1.getValueTy() == Value.ValueType.ConstantInt && op2.getValueTy() == Value.ValueType.ConstantInt) {
                    int result = 0;
                    int lhs = ((ConstantInt) op1).getVal();
                    int rhs = ((ConstantInt) op2).getVal();
                    switch (instr.getValueTy()) {
                        case add -> result = lhs + rhs;
                        case sub -> result = lhs - rhs;
                        case mul -> result = lhs * rhs;
                        case sdiv -> result = (rhs == 0 ? 0 : lhs / rhs);
                        case srem -> result = (rhs == 0 ? 0 : lhs % rhs);
                        case and -> result = lhs & rhs;
                        case or -> result = lhs | rhs;
                    }
                    instr.replaceAllUsesWith(new ConstantInt(instr.getType(), result));
                    instr.remove();
                    finished = false;
                } else {
                    int hash1 = op1 instanceof ConstantInt ? ((ConstantInt) op1).getVal() : op1.hashCode();
                    hash1 = hash1 * 31 + instr.getValueTy().ordinal();
                    hash1 = hash1 * 31 + (op2 instanceof ConstantInt ? ((ConstantInt) op2).getVal() : op2.hashCode());
                    int hash2 = -1;
                    if (instr.getValueTy() == Value.ValueType.add || instr.getValueTy() == Value.ValueType.mul || instr.getValueTy() == Value.ValueType.and || instr.getValueTy() == Value.ValueType.or) {
                        hash2 = op2 instanceof ConstantInt ? ((ConstantInt) op2).getVal() : op2.hashCode();
                        hash2 = hash2 * 31 + instr.getValueTy().ordinal();
                        hash2 = hash2 * 31 + (op1 instanceof ConstantInt ? ((ConstantInt) op1).getVal() : op1.hashCode());
                    }
                    if (map.containsKey(hash1)) {
                        instr.replaceAllUsesWith(map.get(hash1));
                        instr.remove();
                        finished = false;
                    } else if (map.containsKey(hash2) && hash2 != -1) {
                        instr.replaceAllUsesWith(map.get(hash2));
                        instr.remove();
                        finished = false;
                    } else {
                        map.put(hash1, instr);
                        if (hash2 != -1) map.put(hash2, instr);
                    }
                }
            } else if (instr instanceof Icmp) {
                Value op1 = instr.getOperand(0);
                Value op2 = instr.getOperand(1);
                if (op1.getValueTy() == Value.ValueType.ConstantInt && op2.getValueTy() == Value.ValueType.ConstantInt) {
                    int result = 0;
                    switch (((Icmp) instr).getOp()) {
                        case eq -> result = ((ConstantInt) op1).getVal() == ((ConstantInt) op2).getVal() ? 1 : 0;
                        case sle -> result = ((ConstantInt) op1).getVal() <= ((ConstantInt) op2).getVal() ? 1 : 0;
                        case ne -> result = ((ConstantInt) op1).getVal() != ((ConstantInt) op2).getVal() ? 1 : 0;
                        case sge -> result = ((ConstantInt) op1).getVal() >= ((ConstantInt) op2).getVal() ? 1 : 0;
                        case sgt -> result = ((ConstantInt) op1).getVal() > ((ConstantInt) op2).getVal() ? 1 : 0;
                        case slt -> result = ((ConstantInt) op1).getVal() < ((ConstantInt) op2).getVal() ? 1 : 0;
                    }
                    instr.replaceAllUsesWith(new ConstantInt(instr.getType(), result));
                    instr.remove();
                    finished = false;
                }
            } else if (instr instanceof Trunc || instr instanceof Zext) {
                Value op = instr.getOperand(0);
                if (op.getValueTy() == Value.ValueType.ConstantInt) {
                    instr.replaceAllUsesWith(new ConstantInt(instr.getType(), ((ConstantInt) op).getVal()));
                    instr.remove();
                    finished = false;
                }
            }
        }
        return finished;
    }
}
