package backend.lir.mipsOperand;

public class MpReg extends MpOpd {
    private static int count = 0;
    private String name;
    public MpReg(String name) {
        this.name = (null == name ? "vr" + count++ : name);
    }
    public int getCount() { return MpReg.count; }
    public String toString() { return name; }
}
