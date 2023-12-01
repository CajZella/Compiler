package backend.lir.mipsOperand;

public class MpReg extends MpOpd {
    private static int count = 0;
    private String name = null;
    private MpPhyReg phyReg = null;
    private int loopDepth = 0;
    private int useTime = 0;
    public MpReg() { this.name = "vr" + count++; }
    public MpReg(MpPhyReg phyReg) { this.phyReg = phyReg; }
    public void setLoopDepth(int loopDepth) { this.loopDepth = loopDepth; }
    public void decUseTime() {
        if (isColored())
            this.useTime = 0;
        else
            this.useTime--;
    }
    public void incUseTime() {
        if (isColored())
            this.useTime = 0;
        else
            this.useTime++;
    }
    public boolean isColored() { return null == name; }
    public int getLoopDepth() { return this.loopDepth; } //todo: 在ir parser中设置循环
    public int getUseTime() { return this.useTime; } // todo: 在code generator中设置
    public String getName() { return this.name; }
    public MpPhyReg getPhyReg() { return this.phyReg; }
    public String toString() { return null == name ? phyReg.toString() : name; }
    @Override
    public int hashCode() { // todo: check
        return 31 + ( null == name ? phyReg.hashCode() : name.hashCode());
    }
    public boolean equal(Object obj) {
        if (!(obj instanceof MpReg))
            return false;
        MpReg objReg = (MpReg) obj;
        if (null == name && null == objReg.getName())
            return phyReg.equals(objReg.getPhyReg());
        else if (null == phyReg && null == objReg.getPhyReg())
            return name.equals(objReg.getName());
        else
            return false;
    }
}
