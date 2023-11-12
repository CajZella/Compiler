package ir.instrs;

import ir.BasicBlock;
import ir.types.PointerType;

/*
    <result> = alloca <type>
 */
public class Alloca extends Instr {
    public Alloca(PointerType type, BasicBlock pBB) {
        super(ValueType.alloca, type, pBB);
    }
    public boolean isArrayAlloc() { return ((PointerType)type).getReferencedType().isArrayTy(); }
    @Override
    public String toString() {
        return String.format("%s = alloca %s", name, ((PointerType)type).getReferencedType());
    }
}
