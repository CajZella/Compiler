package backend.lir.mipsOperand;

public class MpReg extends MpOpd {
    private static int count = 0;
    private String name = null;
    private MpPhyReg phyReg = null;
    public MpReg() { this.name = "vr" + count++; }
    public MpReg(MpPhyReg phyReg) { this.phyReg = phyReg; }
    public boolean isPrecolored() { return null == name; }
    public String getName() { return this.name; }
    public MpPhyReg getPhyReg() { return this.phyReg; }
    public String toString() { return null == name ? phyReg.toString() : name; }
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
