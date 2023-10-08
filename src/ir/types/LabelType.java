package ir.types;

public class LabelType extends Type {
    public boolean isIntegerTy(int bitWidth) { return false; }
    @Override
    public String toString() {
        return "label";
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) { return false; }
        return ((Type)obj).isLabelTy();
    }
}
