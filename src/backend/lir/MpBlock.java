package backend.lir;

import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsOperand.MpLabel;
import util.MyLinkedList;
import util.MyLinkedNode;

import java.util.HashSet;

public class MpBlock extends MyLinkedNode {
    //todo:基本块的前驱映射
    private MpLabel label;
    private MyLinkedList<MpInstr> mpInstrs = new MyLinkedList<>();
    private MpFunction function;
    private HashSet<MpBlock> predecessors = new HashSet<>();
    private HashSet<MpBlock> successors = new HashSet<>();
    public MpBlock(String name, MpFunction function) {
        this.label = null == name ? null : new MpLabel(name);
        this.function = function;
        this.function.addMpBlock(this);
    }
    public void addPredecessor(MpBlock block) { predecessors.add(block); }
    public void addSuccessor(MpBlock block) { successors.add(block); }
    public HashSet<MpBlock> getPredecessors() { return predecessors; }
    public HashSet<MpBlock> getSuccessors() { return successors; }
    public MpLabel getLabel() { return this.label; }
    public MpInstr getLastMpInstr() { return mpInstrs.getTail(); }
    public void removeInstr(MpInstr instr) { mpInstrs.remove(instr); }
    public void addMpBlock(MpInstr mpInstr) { this.mpInstrs.insertAtTail(mpInstr); }
    public MyLinkedList<MpInstr> getMpInstrs() { return mpInstrs; }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(null == label ? "" : label + ":\n");
        for (MpInstr mpInstr : mpInstrs)
            builder.append("\t" + mpInstr + "\n");
        return builder.toString();
    }
}
