package backend.lir;

import backend.lir.mipsOperand.MpLabel;
import backend.lir.mipsOperand.MpReg;
import util.MyLinkedList;

import java.util.ArrayList;
import java.util.HashSet;

public class MpFunction {
    private MpLabel label;
    private int stackSize = 0;
    private int argSize;
    private HashSet<MpReg> regUsed = new HashSet<>();
    private MyLinkedList<MpBlock> mpBlocks = new MyLinkedList<>();
    public MpFunction(String name, int argSize) {
        this.label = new MpLabel(name, this);
        this.argSize = argSize;
    }
    public void addMpBlock(MpBlock block) { this.mpBlocks.insertAtTail(block); }
    public void setStackSize(int size) { this.stackSize = size; }
    public int getStackSize() { return this.stackSize; }
    public MpLabel getLabel() { return this.label; }
    public boolean isMain() { return this.label.getName().equals("main"); }
    public MyLinkedList<MpBlock> getMpBlocks() { return this.mpBlocks; }
    public int getArgSize() { return this.argSize; }
    public void addRegUsed(MpReg reg) { this.regUsed.add(reg); }
    public HashSet<MpReg> getRegUsed() { return this.regUsed; }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(label + ":\n");
        for (MpBlock block : mpBlocks)
            builder.append(block);
        return builder.toString();
    }
}
