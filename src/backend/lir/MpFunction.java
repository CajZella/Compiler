package backend.lir;

import backend.lir.mipsOperand.MpLabel;
import util.MyLinkedList;

public class MpFunction {
    private MpLabel label;
    private int stackSize = 0;
    private MpBlock entryBlock;
    private MpBlock exitBlock;
    private MyLinkedList<MpBlock> mpBlocks = new MyLinkedList<>();
    public MpFunction(String name) { this.label = new MpLabel(name); }
    public void addMpBlock(MpBlock block) { this.mpBlocks.insertAtTail(block); }
    public void setStackSize(int size) { this.stackSize = size; }
    public int getStackSize() { return this.stackSize; }
    public MpLabel getLabel() { return this.label; }
    public MyLinkedList<MpBlock> getMpBlocks() { return this.mpBlocks; }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(label + ":\n");
        for (MpBlock block : mpBlocks)
            builder.append(block);
        return builder.toString();
    }
}
