package ir.types;

public abstract class Type {
    public enum TypeID {
        VoidTyID,
        LabelTyID,
        IntegerTyID,
        FunctionTyID,
        PointerTyID,
        ArrayTyID,
    }
    TypeID typeID = null;

    public TypeID getTypeID() { return this.typeID; }
    public boolean isVoidTy() { return this.typeID == TypeID.VoidTyID; }
    public boolean isLabelTy() { return this.typeID == TypeID.LabelTyID; }
    public boolean isIntegerTy() { return this.typeID == TypeID.IntegerTyID; }
    public boolean isFunctionTy() { return this.typeID == TypeID.FunctionTyID; }
    public boolean isPointerTy() { return this.typeID == TypeID.PointerTyID; }
    public boolean isArrayTy() { return this.typeID == TypeID.ArrayTyID; }
    public abstract boolean isIntegerTy(int bitWidth);
    public boolean isEmptyTy() { return null == this.typeID; }
}
