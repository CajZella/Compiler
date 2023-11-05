package backend.lir.mipsOperand;

public class MpImm extends MpOpd {
    private int val;
    public MpImm(int val) { this.val = val; }
    public int getVal() { return this.val; }
    public void setVal(int val) { this.val = val; }
    public void addVal(int val) { this.val += val; }
    public String toString() { return String.valueOf(this.val); }
}