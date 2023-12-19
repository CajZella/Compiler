package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import ir.Value;
import ir.instrs.Br;
import ir.instrs.Instr;
import util.MyLinkedList;

public class DeadControlFlowElimination {
    private Module module;
    public DeadControlFlowElimination(Module module) {
        this.module = module;
    }
    public boolean run() {
        boolean finished = false;
        boolean all = true;
        while (!finished) {
            finished = true;
            finished &= mergeBlock();
            finished &= promoteBranch();
            all &= finished;
        }
        return all;
    }


    private boolean mergeBlock() {
        boolean finished = true;
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                if (block.getSuccBBs().size() == 1
                        && block.getSuccBBs().iterator().next().getPrecBBs().size() == 1) {
                    finished = false;
                    BasicBlock succBlock = block.getSuccBBs().iterator().next();
                    MyLinkedList<Instr> instrs = block.getInstrs();
                    instrs.remove(instrs.getTail());
                    for (Instr instr : succBlock.getInstrs())
                        instr.setParent(block);
                    instrs.addAll(succBlock.getInstrs());
                    block.getSuccBBs().remove(succBlock);
                    block.getSuccBBs().addAll(succBlock.getSuccBBs());
                    for (BasicBlock succ : succBlock.getSuccBBs()) {
                        succ.getPrecBBs().remove(succBlock);
                        succ.getPrecBBs().add(block);
                    }
                    succBlock.remove();
                }
            }
        }
        return finished;
    }
    private boolean promoteBranch() {
        boolean finished = true;
        for (Function function : module.getFunctions()) {
            for (BasicBlock block : function.getBlocks()) {
                Instr entry = block.getEntryInstr();
                if (entry.getValueTy() == Value.ValueType.br && ((Br) entry).isCondBr() && !block.getPrecBBs().isEmpty()) {
                    for (BasicBlock precBlock : block.getPrecBBs()) {
                        Br terminator = (Br) precBlock.getTerminator();
                        if (!terminator.isCondBr()) {
                            finished = false;
                            terminator.replaceAllUses(entry.getOperands());
                            precBlock.getSuccBBs().remove(block);
                            precBlock.getSuccBBs().add(((Br) entry).getTrueBB());
                            ((Br) entry).getTrueBB().getPrecBBs().add(precBlock);
                            precBlock.getSuccBBs().add(((Br) entry).getFalseBB());
                            ((Br) entry).getFalseBB().getPrecBBs().add(precBlock);
                        }
                    }
                    block.remove();
                }
            }
        }
        return finished;
    }
}
