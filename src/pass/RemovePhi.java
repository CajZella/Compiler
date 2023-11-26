package pass;

import ir.BasicBlock;
import ir.Function;
import ir.Module;
import ir.Value;
import ir.instrs.Br;
import ir.instrs.Instr;
import ir.instrs.Phi;
import util.MyLinkedList;

import java.util.HashMap;
import java.util.HashSet;


public class RemovePhi {
    private Module module;
    private Function curFunc;
    public RemovePhi(ir.Module module) {
        this.module = module;
    }
    public void run() {
        for (Function function : module.getFunctions()) {
            curFunc = function;
            removeFuncPhi();
        }
    }
    private void removeFuncPhi() {
        MyLinkedList<BasicBlock> basicBlocks = curFunc.getBlocks();
        // step1：插入PCs
        BasicBlock firstAdd = null;
        for (BasicBlock basicBlock : basicBlocks) {
            if (basicBlock.equals(firstAdd))
                break;
            HashSet<BasicBlock> precBBs = new HashSet<>();
            for (BasicBlock precBlock : basicBlock.getPrecBBs()) {
                PCs pcs = new PCs();
                if (precBlock.getSuccBBs().size() > 1 && basicBlock.getPrecBBs().size() > 1) {
                    BasicBlock pcBlock = new BasicBlock(curFunc);
                    if (null == firstAdd)
                        firstAdd = pcBlock;
                    Br terminator = (Br) precBlock.getTerminator();
                    terminator.replaceUsesOfWith(basicBlock, pcBlock);
                    precBlock.getSuccBBs().remove(basicBlock);
                    precBlock.getSuccBBs().add(pcBlock);
                    pcBlock.getPrecBBs().add(precBlock);
                    pcBlock.addInstr(new Br(pcBlock, basicBlock));
                    pcBlock.getSuccBBs().add(basicBlock);
                    precBBs.add(pcBlock);
                    pcBlock.setPcs(pcs);
                    precBlock.setPcBlock(pcBlock);
                } else {
                    precBBs.add(precBlock);
                    precBlock.setPcs(pcs);
                }
            }
            basicBlock.setPrecBBs(precBBs);
        }
        // step2: 填充PCs
        for (BasicBlock basicBlock : basicBlocks) {
            for (Instr instr : basicBlock.getInstrs()) {
                if (!(instr instanceof Phi))
                    break;
                Phi phi = (Phi) instr;
                for (int i = 0; i < phi.operandsSize(); i++) {
                    Value operand = phi.getOperand(i);
                    BasicBlock phiBB = phi.getBlock(i);
                    PCs pcs = phiBB.getPcs();
                    if (null == pcs)
                        pcs = phiBB.getPcBlock().getPcs();
                    pcs.add(operand, phi);
                }
                // phi.remove();
            }
        }
    }
}
