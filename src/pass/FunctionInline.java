package pass;

import ir.Argument;
import ir.BasicBlock;
import ir.Module;
import ir.Function;
import ir.Use;
import ir.User;
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
import ir.types.DataType;
import ir.types.IntegerType;
import ir.types.PointerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class FunctionInline {
    private Module module;
    private HashSet<Function> recursiveFunctions = new HashSet<>();
    public FunctionInline(Module module) {
        this.module = module;
    }
    public void run() {
        init();
        /* step1. 标记递归函数 */
        for (Function function : module.getFunctions())
            if (function.isMain()) {
                dfsMarkRecursive(function, new HashSet<>());
                break;
            }

        /* step2. 实现函数内联 */
        for (Function function : module.getFunctions())
            if (function.isMain()) {
                inlineFunction(function, new HashSet<>());
                break;
            }
    }
    private void init() {
        recursiveFunctions.clear();
    }
    private void dfsMarkRecursive(Function function, HashSet<Function> visited) {
        visited.add(function);
        if (function.getCallees().contains(function))
            recursiveFunctions.add(function);
        for (Function callee : function.getCallees())
            if (!visited.contains(callee))
                dfsMarkRecursive(callee, visited);
    }
    /*
     * 在调用者的callInst处插入一个新的basicblock(从这里开始进入内联函数)
     * 在这个新的basicblock里填充指令，即被内联函数的指令
     * 删除callInst并修正函数之间的调用关系
     * 修正basicblock的前驱后继关系
     * 将调用函数的形式参数换为为传入参数
     * 处理被内联的函数中的ret和call指令，多个ret合并成一条phi指令
     */
    private void inlineFunction(Function function, HashSet<Function> visited) {
        if (visited.contains(function) || function.isBuiltin())
            return;
        visited.add(function);
        for (Function callee : function.getCallees())
            inlineFunction(callee, visited);
        for (BasicBlock block : function.getBlocks())
            for (Instr instr : block.getInstrs()) {
                if (instr instanceof Call) {
                    Call call = (Call) instr;
                    Function callee = call.getFunction();
                    if (callee.isBuiltin() || function.equals(callee) || recursiveFunctions.contains(callee))
                        continue;
                    inlineSingleFunction(call);
                }
            }
        // 修正函数调用关系
    }
    private void inlineSingleFunction(Call call) {
        Function caller = call.getParent().getParent();
        Function callee = call.getFunction();
        if (callee.getMipsName().equals("func1")) {
            int x = 1;
        }
        BasicBlock block = call.getParent();
        /* step1. 将call之前和之后分成两个block */
        BasicBlock nextBlock = new BasicBlock(caller);
        boolean isInNextBlock = false;
        ArrayList<Instr> instrs = new ArrayList<>();
        Iterator<Instr> iterator = block.getInstrs().iterator();
        while (iterator.hasNext()) {
            Instr instr = iterator.next();
            if (isInNextBlock) {
                iterator.remove();
                instrs.add(instr);
            }
            if (instr.equals(call))
                isInNextBlock = true;
        }
        for (Instr instr : instrs) {
            instr.setParent(nextBlock);
            nextBlock.addInstr(instr);
        }
        // 更新前驱后继关系
        nextBlock.setSuccBBs(block.getSuccBBs());
        for (BasicBlock succ : block.getSuccBBs()) {
            succ.getPrecBBs().remove(block);
            succ.addPrecBBs(nextBlock);
        }
        // 更新phi
        for (BasicBlock succ : block.getSuccBBs()) {
            for (Instr instr : succ.getInstrs()) {
                if (!(instr instanceof Phi))
                    break;
                Phi phi = (Phi) instr;
                for (int i = 0; i < phi.getPhiBBs().size(); i++) {
                    if (phi.getBlock(i).equals(block)) {
                        phi.replacePhiBB(i, nextBlock);
                        break;
                    }
                }
            }
        }
        block.getSuccBBs().clear();
        /* step2. 将callee的指令插入到caller中 */
        LinkedList<BasicBlock> cloneBlocks = functionClone(call, callee);

        /* step3. 替换call instruction，修正基本块前驱后继关系 */
        Br br = new Br(block, cloneBlocks.get(0));
        call.insertBefore(br);
        block.addSuccBBs(cloneBlocks.get(0));
        cloneBlocks.get(0).addPrecBBs(block);

        /* step4. 处理内联函数中的ret指令，多个ret指令合并成一条phi指令 */
        ArrayList<Ret> rets = new ArrayList<>();
        for (BasicBlock cloneBlock : cloneBlocks) {
            Instr terminator = cloneBlock.getTerminator();
            if (terminator instanceof Ret) {
                rets.add((Ret) terminator);
                terminator.insertBefore(new Br(cloneBlock, nextBlock));
                cloneBlock.addSuccBBs(nextBlock);
                nextBlock.addPrecBBs(cloneBlock);
            }
        }
        if (call.getType().isIntegerTy()) {
            Phi phi = new Phi(call.getType(), nextBlock);
            nextBlock.getInstrs().insertAtHead(phi);
            for (Ret ret : rets) {
                phi.addIncoming(ret.getOperand(0), ret.getParent());
                ret.dropAllReferences();
                ret.remove();
            }
            call.replaceAllUsesWith(phi);
        } else {
            for (Ret ret : rets) {
                ret.dropAllReferences();
                ret.remove();
            }
        }
        call.dropAllReferences();
        call.remove();
    }
    private HashMap<Value, Value> valueMap = new HashMap<>();;
    private LinkedList<BasicBlock> functionClone(Call call, Function function) {
        LinkedList<BasicBlock> basicBlocks = new LinkedList<>();
        valueMap.clear();
        for (BasicBlock block : function.getBlocks()) {
            BasicBlock newBlock = new BasicBlock(call.getParent().getParent());
            basicBlocks.add(newBlock);
            valueMap.put(block, newBlock);
        }
        for (BasicBlock block : function.getBlocks()) {
            BasicBlock newBlock = (BasicBlock) valueMap.get(block);
            for (BasicBlock prec : block.getPrecBBs())
                newBlock.addPrecBBs((BasicBlock) valueMap.get(prec));
            for (BasicBlock succ : block.getSuccBBs())
                newBlock.addSuccBBs((BasicBlock) valueMap.get(succ));
        }
        for (int i = 1; i < call.operandsSize(); i++)
            valueMap.put(function.getArguments().get(i - 1), call.getOperand(i));
        for (BasicBlock block : function.getBlocks()) {
            BasicBlock curBlock = (BasicBlock) valueMap.get(block);
            for (Instr instr : block.getInstrs()) {
                Instr newInstr = null;
                if (instr instanceof Alloca) {
                    Alloca alloca = (Alloca) instr;
                    newInstr = new Alloca((PointerType) alloca.getType(), curBlock);
                } else if (instr instanceof Alu) {
                    Alu alu = (Alu) instr;
                    Value lhs = getOperand(alu.getOperand(0));
                    Value rhs = getOperand(alu.getOperand(1));
                    newInstr = new Alu(alu.getValueTy(), (IntegerType) alu.getType(), curBlock, lhs, rhs);
                } else if (instr instanceof Br) {
                    Br br = (Br) instr;
                    if (br.isCondBr())
                        newInstr = new Br(curBlock, getOperand(br.getOperand(0)), getOperand(br.getOperand(1)), getOperand(br.getOperand(2)));
                    else
                        newInstr = new Br(curBlock, getOperand(br.getOperand(0)));
                } else if (instr instanceof Call) {
                    Call callInstr = (Call) instr;
                    ArrayList<Value> args = new ArrayList<>();
                    for (int i = 0; i < callInstr.operandsSize(); i++)
                        args.add(getOperand(callInstr.getOperand(i)));
                    newInstr = new Call((DataType) callInstr.getType(),curBlock, args.toArray(new Value[args.size()]));
                } else if (instr instanceof GetElementPtr) {
                    GetElementPtr gep = (GetElementPtr) instr;
                    ArrayList<Value> operands = new ArrayList<>();
                    for (int i = 0; i < gep.operandsSize(); i++)
                        operands.add(getOperand(gep.getOperand(i)));
                    newInstr = new GetElementPtr((PointerType) gep.getType(), curBlock, operands.toArray(new Value[operands.size()]));
                } else if (instr instanceof Icmp) {
                    Icmp icmp = (Icmp) instr;
                    Value lhs = getOperand(icmp.getOperand(0));
                    Value rhs = getOperand(icmp.getOperand(1));
                    newInstr = new Icmp(icmp.getOp(), curBlock, lhs, rhs);
                } else if (instr instanceof Load) {
                    Load load = (Load) instr;
                    newInstr = new Load((DataType) load.getType(), curBlock, getOperand(load.getOperand(0)));
                } else if (instr instanceof Store) {
                    Store store = (Store) instr;
                    newInstr = new Store(curBlock, getOperand(store.getOperand(0)), getOperand(store.getOperand(1)));
                } else if (instr instanceof Ret) {
                    Ret ret = (Ret) instr;
                    if (ret.hasReturnValue())
                        newInstr = new Ret(curBlock, getOperand(ret.getOperand(0)));
                    else
                        newInstr = new Ret(curBlock);
                } else if (instr instanceof Trunc) {
                    Trunc trunc = (Trunc) instr;
                    newInstr = new Trunc(trunc.getType(), curBlock, getOperand(trunc.getOperand(0)));
                } else if (instr instanceof Zext) {
                    Zext zext = (Zext) instr;
                    newInstr = new Zext(zext.getType(), curBlock, getOperand(zext.getOperand(0)));
                } else if (instr instanceof Phi) {
                    Phi phi = (Phi) instr;
                    newInstr = new Phi(phi.getType(), curBlock);
                }
                valueMap.put(instr, newInstr);
                curBlock.addInstr(newInstr);
            }
        }

        // 其他指令用phi时，phi未创建好
        for (Map.Entry<Value, Value> entry : valueMap.entrySet()) {
            if (entry.getKey() instanceof Phi) {
                Phi phi = (Phi) entry.getKey();
                Phi newPhi = (Phi) entry.getValue();
                for (int i = 0; i < phi.operandsSize(); i++)
                    newPhi.addIncoming(getOperand(phi.getOperand(i)), (BasicBlock) getOperand(phi.getBlock(i)));
            }
        }
        for (Map.Entry<Value, Value> entry : valueMap.entrySet()) {
            if (entry.getKey() instanceof Phi) {
                Phi phi = (Phi) entry.getKey();
                Phi newPhi = (Phi) entry.getValue();
                for(Use use : phi.getUseList()) {
                    User user = use.getUser();
                    for (int i = 0; i < user.getUserUses().size(); i++) {
                        Use userUse = user.getUserUses().get(i);
                        if (userUse.getVal().equals(phi)) {
                            ((User)getOperand(use.getUser())).replaceUsesOfWith(i, newPhi);
                        }
                    }
                }
            }
        }
        return basicBlocks;
    }
    private Value getOperand(Value value) {
        if (value instanceof Instr) {
            if (valueMap.containsKey(value))
                return valueMap.get(value);
            else // 可能为空
                return new Instr();
        } else if (value instanceof Argument) {
            return valueMap.get(value);
        } else if (value instanceof BasicBlock)
            return valueMap.get(value);
        else return value;
    }
}
