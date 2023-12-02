package backend.lir;

import backend.lir.mipsInstr.MpBranch;
import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsInstr.MpJump;
import backend.lir.mipsOperand.MpLabel;
import util.MyLinkedList;
import util.MyLinkedNode;

import java.util.HashSet;

public class MpBlock extends MyLinkedNode {
    //todo:基本块的前驱映射
    public static int cnt = 0;
    private MpLabel label;
    private MyLinkedList<MpInstr> mpInstrs = new MyLinkedList<>();
    private MpFunction function;
    private HashSet<MpBlock> precMBs = new HashSet<>();
    private HashSet<MpBlock> succMBs = new HashSet<>();
    public MpBlock(String name, MpFunction function) {
        this.label = new MpLabel(name, this);
        this.function = function;
        this.function.addMpBlock(this);
    }
    public MpLabel getMpLabel() { return label; }
    public void addPrecMB(MpBlock mb) { precMBs.add(mb); }
    public void addSuccMB(MpBlock mb) { succMBs.add(mb); }
    public HashSet<MpBlock> getPrecMBs() { return precMBs; }
    public HashSet<MpBlock> getSuccMBs() { return succMBs; }
    public MpLabel getLabel() { return this.label; }
    public MpInstr getFirstMpInstr() { return mpInstrs.getHead(); }
    public MpInstr getLastMpInstr() { return mpInstrs.getTail(); }
    public void removeInstr(MpInstr instr) { mpInstrs.remove(instr); }
    public void addMpInstr(MpInstr mpInstr) { this.mpInstrs.insertAtTail(mpInstr); }
    public MyLinkedList<MpInstr> getMpInstrs() { return mpInstrs; }
    public MpFunction getFunction() { return function; }
    public void replaceBJlabel(MpLabel oldLabel, MpLabel newLabel) {
        for (MpInstr instr : mpInstrs) {
            if (instr instanceof MpBranch && ((MpBranch) instr).getLabel() == oldLabel) {
                ((MpBranch) instr).replaceLabel(newLabel);
            } else if (instr.getInstrType() == MpInstr.MipsInstrType.j && ((MpJump) instr).getLabel() == oldLabel) {
                ((MpJump) instr).replaceLabel(newLabel);
            }
        }
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(null == label ? "" : label + ":\n");
        for (MpInstr mpInstr : mpInstrs)
            builder.append("\t" + mpInstr + "\n");
        return builder.toString();
    }
}
