package backend.lir.mipsOperand;

public class MpStackOffset extends MpOpd {
    private MpReg base;
    private MpImm offset;
    public MpStackOffset(MpReg base, MpImm offset) {
        this.base = base;
        this.offset = offset;
    }
    public MpReg getBase() { return base; }
    public MpImm getOffset() { return offset; }
    public String toString() { return String.format("%d", offset); }
}
