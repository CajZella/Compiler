package backend.lir.mipsOperand;

import backend.lir.MpBlock;
import backend.lir.MpFunction;

// include function, block, data
public class MpLabel {
    private static int count = 0;
    private String name;
    private MpFunction function = null;
    private MpBlock block = null;
    public MpLabel(String name, MpFunction function) {
        this.name = (null == name ? "label" + count++ : name);
        this.function = function;
    }
    public MpLabel(String name, MpBlock block) {
        this.name = (null == name ? "label" + count++ : name);
        this.block = block;
    }
    public MpFunction getFunction() { return this.function; }
    public MpBlock getBlock() { return this.block; }
    public String getName() { return this.name; }
    public String toString() { return this.name; }
}
