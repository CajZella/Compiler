package ir.types;

/* alloca instruction's return type
   function's formal parameters
   pointer to array
 */
public class PointerType extends DataType {
    private final Type referencedType; // the object that the pointer point to
    public PointerType(Type referencedType) {
        super(TypeID.PointerTyID);
        this.referencedType = referencedType;
    }
    public boolean isIntegerTy(int bitWidth) { return false; }
    public Type getReferencedType() { return this.referencedType; }
    @Override public String toString() { return this.referencedType + "*"; }
    @Override
    public boolean equals(Object object) {
        if (object instanceof PointerType) {
            return this.referencedType.equals(((PointerType) object).getReferencedType());
        } else {
            return false;
        }
    }
    //todo
    public int size() { return referencedType.size(); }
}
