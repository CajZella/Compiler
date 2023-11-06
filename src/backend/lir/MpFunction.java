package backend.lir;

import backend.lir.mipsOperand.MpLabel;
import util.MyLinkedList;

public class MpFunction {
    private MpLabel label;
    private int stackSize = 0;
    private int argSize;
    private MyLinkedList<MpBlock> mpBlocks = new MyLinkedList<>();
    public MpFunction(String name) { this.label = new MpLabel(name); }
    public void addMpBlock(MpBlock block) { this.mpBlocks.insertAtTail(block); }
    public void setStackSize(int size) { this.stackSize = size; }
    public int getStackSize() { return this.stackSize; }
    public MpLabel getLabel() { return this.label; }
    public boolean isMain() { return this.label.getName().equals("main"); }
    public MyLinkedList<MpBlock> getMpBlocks() { return this.mpBlocks; }
    public void setArgSize(int size) { this.argSize = size; }
    public boolean isArgGreater4() { return this.argSize > 4; }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(label + ":\n");
        for (MpBlock block : mpBlocks)
            builder.append(block);
        return builder.toString();
    }
}
