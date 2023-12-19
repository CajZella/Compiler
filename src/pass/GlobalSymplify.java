package pass;

import ir.BasicBlock;
import ir.GlobalVariable;
import ir.Module;
import ir.Function;
import ir.Use;
import ir.User;
import ir.Value;
import ir.constants.ConstantInt;
import ir.instrs.Alloca;
import ir.instrs.GetElementPtr;
import ir.instrs.Instr;
import ir.instrs.Load;
import ir.instrs.Store;
import ir.types.PointerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class GlobalSymplify {
    private Module module;
    private HashMap<GlobalVariable, HashSet<Function>> gvMap = new HashMap<>();
    public GlobalSymplify(Module module) {
        this.module = module;
    }
    public void run() {
        gvMap.clear();
        Iterator<GlobalVariable> iterator = module.getGlobalVariables().iterator();
        while(iterator.hasNext()) {
            GlobalVariable gv = iterator.next();
            /* 如果没有用过gv，直接删除 */
            if (gv.getUseList().isEmpty())
                iterator.remove();
            gvMap.put(gv, new HashSet<>());
            for (Use use : gv.getUseList()) {
                Instr user = (Instr) use.getUser();
                gvMap.get(gv).add(user.getParent().getParent());
            }
        }
        /* 只有use没有def */
        iterator = module.getGlobalVariables().iterator();
        while(iterator.hasNext()) {
            GlobalVariable gv = iterator.next();
            if (checkOnlyUse(gv) && ((PointerType)gv.getType()).getReferencedType().isIntegerTy()) {
                ArrayList<Load> loads = new ArrayList<>();
                for (Use use : gv.getUseList()) {
                    loads.add((Load) use.getUser());
                }
                for (Load load : loads) {
                    load.replaceAllUsesWith(gv.getInitializer());
                    load.dropAllReferences();
                    load.remove();
                }

                iterator.remove();
            }
        }
        /* 只有一个函数使用 */
        iterator = module.getGlobalVariables().iterator();
        while(iterator.hasNext()) {
            GlobalVariable gv = iterator.next();
            if (gvMap.get(gv).size() == 1 && ((PointerType)gv.getType()).getReferencedType().isIntegerTy()) {
                Function function = gvMap.get(gv).iterator().next();
                BasicBlock block = function.getEntryBlock();
                Alloca alloca = new Alloca((PointerType) gv.getType(), block);
                block.getInstrs().insertAtHead(alloca);
                Store store = new Store(block, gv.getInitializer(), alloca);
                alloca.insertAfter(store);
                gv.replaceAllUsesWith(alloca);
                iterator.remove();
            }
        }
    }
    private boolean checkOnlyUse(GlobalVariable gv) {
        Queue<Value> queue = new LinkedList<>();
        queue.add(gv);
        while (!queue.isEmpty()) {
            Value value = queue.poll();
            for (Use use : value.getUseList()) {
                User user = use.getUser();
                if (user instanceof Store && user.getOperand(1).equals(value))
                    return false;
                if (user instanceof Load && user.getOperand(0).equals(value))
                    queue.add(user);
            }
        }
        return true;
    }
}
