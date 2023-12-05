package ir.instrs;

import ir.BasicBlock;
import ir.Use;
import ir.types.PointerType;

import java.util.ArrayList;

/*
    <result> = alloca <type>
 */
public class Alloca extends Instr {
    public Alloca(PointerType type, BasicBlock pBB) {
        super(ValueType.alloca, type, pBB);
    }
    public boolean isArrayAlloc() { return ((PointerType)type).getReferencedType().isArrayTy(); }
    public ArrayList<Store> getStores() {
        ArrayList<Store> stores = new ArrayList<>();
        for(Use use : useList) {
            if (use.getUser() instanceof Store)
                stores.add((Store) use.getUser());
            else if (use.getUser() instanceof GetElementPtr) {
                GetElementPtr getElementPtr = (GetElementPtr) use.getUser();
                if (getElementPtr.getNext() instanceof Store)
                    stores.add((Store) getElementPtr.getNext());
            }
        }
        return stores;
    }
    @Override
    public String toString() {
        return String.format("%s = alloca %s", name, ((PointerType)type).getReferencedType());
    }
}
