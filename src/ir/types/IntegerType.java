package ir.types;

public class IntegerType extends DataType {
    private final int bitWidth;
    public IntegerType(int bitWidth) { this.bitWidth = bitWidth; }
    public boolean isIntegerTy(int bitWidth) {
        return this.bitWidth == bitWidth;
    }
    @Override
    public String toString() { return "i" + bitWidth; }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Type)) { return false; }
        Type objType = (Type) obj;
        return objType.isIntegerTy(bitWidth);
    }
}
