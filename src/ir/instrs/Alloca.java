package ir.instrs;

import ir.BasicBlock;
import ir.types.PointerType;

/*
    <result> = alloca <type>
 */
public class Alloca extends Instr {
    public Alloca(int num, PointerType type, BasicBlock pBB) {
        super(ValueType.alloca, String.format("%%d", num), type, pBB);
    }
    @Override
    public String toString() {
        return String.format("%s = alloca %s", name, ((PointerType)type).getReferencedType());
    }
}
