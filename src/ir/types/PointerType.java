package ir.types;

/* alloca instruction's return type
   function's formal parameters
   pointer to array
 */
public class PointerType extends DataType {
    private Type referencedType; // the object that the pointer point to
    public PointerType(Type referencedType) {
        this.referencedType = referencedType;
    }
    public boolean isIntegerTy(int bitWidth) { return false; }
    public Type getReferencedType() { return this.referencedType; }
    @Override public String toString() { return this.referencedType + "*"; }
}
