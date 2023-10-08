package ir.types;

import java.util.ArrayList;

public class ArrayType extends Type {
    private Type elementType;
    private DataType baseType;
    private ArrayList<Integer> dims = new ArrayList<>(); // 第0位对应第1维
    private int length;
    public boolean isIntegerTy(int bitWidth) { return false; }
    public ArrayType(Type elementType, int length) {
        if (elementType instanceof DataType) {
            dims.add(length);
            baseType = (DataType) elementType;
        } else if (elementType instanceof ir.types.ArrayType) {
            ir.types.ArrayType eaType = (ir.types.ArrayType)elementType;
            dims.addAll(eaType.getDims());
            dims.add(length);
            baseType = eaType.getBaseType();
        } else { throw new RuntimeException("array's elements must be data type or array."); }
        this.elementType = elementType;
        this.length = length;
    }
    public ArrayType(ArrayList<Integer> dims, DataType dataType) {
        Type temp = dataType;
        for (int i = 0; i < dims.size() - 1; i++) {
            temp = new ir.types.ArrayType(temp, dims.get(i));
        }
        this.elementType = temp;
        this.length = dims.get(dims.size() - 1);
        this.baseType = dataType;
        this.dims.addAll(dims);
    }
    public Type getElementType() { return this.elementType; }
    public DataType getBaseType() { return this.baseType; }
    public ArrayList<Integer> getDims() { return dims; }
    public int getLength() { return this.length; }
    @Override
    public String toString() {
        return "[" + length + " x " + elementType + "]";
    }
}
