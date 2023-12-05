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
import java.util.Map;


public class RemovePhi {
    private Module module;
    private Function curFunc;
    private HashMap<BasicBlock, HashMap<BasicBlock, BasicBlock>> map = new HashMap<>();
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
        map.clear();
        MyLinkedList<BasicBlock> basicBlocks = curFunc.getBlocks();
        for (BasicBlock block : basicBlocks)
            map.put(block, new HashMap<>());
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
                    map.get(precBlock).put(basicBlock, pcBlock);
                } else {
                    precBBs.add(precBlock);
                    precBlock.setPcs(pcs);
                    map.get(precBlock).put(basicBlock, precBlock);
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
                    BasicBlock selectBB = map.get(phiBB).get(basicBlock);
                    PCs pcs = selectBB.getPcs();
                    pcs.add(operand, phi);
                }
                // phi.remove();
            }
        }
//        for (BasicBlock block : curFunc.getBlocks()) {
//            PCs pcs = block.getPcs();
//            if (pcs != null) {
//                System.out.println(block.getMipsName());
//                for (PCs.ParallelCopy pc : pcs.getOriginPCs()) {
//                    System.out.println(pc.getDst() + " = " + pc.getSrc());
//                }
//            }
//        }
    }
}
