package backend.lir.mipsOperand;

public class MpImm {
    private int imm;
    public MpImm(int imm) { this.imm = imm; }
    public String toString() { return String.valueOf(this.imm); }
}
