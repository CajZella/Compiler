package ir.types;

public class VoidType extends DataType {
    public boolean isIntegerTy(int bitWidth) { return false; }
    @Override
    public String toString() {
        return "void";
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) { return false; }
        return ((Type)obj).isVoidTy();
    }
}
