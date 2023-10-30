package ir.types;

public class VoidType extends DataType {
    public VoidType() { super(TypeID.VoidTyID); }
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
    public int size() {
        assert false: "void type has no size.";
        return 0;
    }
}
