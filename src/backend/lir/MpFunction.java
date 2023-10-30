package backend.lir;

import backend.lir.mipsOperand.MpLabel;

import java.util.LinkedList;

public class MpFunction {
    private MpLabel label;
    private LinkedList<MpBlock> mpBlocks = new LinkedList<>();
    public MpFunction(String name) { this.label = new MpLabel(name); }
    public void addMpBlock(MpBlock block) { this.mpBlocks.add(block); }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(label + ":\n");
        for (MpBlock block : mpBlocks)
            builder.append(block);
        return builder.toString();
    }
}
