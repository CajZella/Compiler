package backend.lir.mipsOperand;

public class MpStackOffset extends MpOpd {
    private MpReg base;
    private MpImm offset;
    private MpData data;
    public MpStackOffset(MpReg base, MpImm offset) {
        this.base = base;
        this.offset = offset;
    }
    public MpStackOffset(MpData data, MpReg base, MpImm offset) {
        this.data = data;
        this.base = base;
        this.offset = offset;
    }
    public MpReg getBase() { return base; }
    public MpImm getOffset() { return offset; }
    public MpData getData() { return data; }
    public String toString() { return String.format("%d", offset); }
}
