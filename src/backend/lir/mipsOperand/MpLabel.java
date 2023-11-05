package backend.lir.mipsOperand;

// include function, block, data
public class MpLabel {
    private static int count = 0;
    private String name;
    public MpLabel(String name) {
        this.name = (null == name ? "label" + count++ : name);
    }
    public String toString() { return this.name; }
}
