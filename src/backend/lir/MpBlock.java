package backend.lir;

import backend.lir.mipsInstr.MpInstr;
import backend.lir.mipsOperand.MpLabel;
import util.MyLinkedList;

public class MpBlock {
    private MpLabel label;
    private MyLinkedList<MpInstr> mpInstrs = new MyLinkedList<>();
    private MpFunction function;
    public MpBlock(String name, MpFunction function) {
        this.label = null == name ? null : new MpLabel(name);
        this.function = function;
        this.function.addMpBlock(this);
    }
    public void addMpBlock(MpInstr mpInstr) { this.mpInstrs.insertAtTail(mpInstr); }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(null == label ? "" : label + ":\n");
        for (MpInstr mpInstr : mpInstrs)
            builder.append("\t" + mpInstr + "\n");
        return builder.toString();
    }
}
