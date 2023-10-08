package ir.types;

import java.util.ArrayList;

// include argument types and return type
public class FunctionType extends Type {
    private ArrayList<DataType> argumentTypes = new ArrayList<>();
    private DataType returnType;
    public boolean isIntegerTy(int bitWidth) { return false; }
    public FunctionType(ArrayList<DataType> argumentTypes, DataType returnType) {
        this.argumentTypes = argumentTypes;
        this.returnType = returnType;
    }
    public ArrayList<DataType> getArgumentTypes() { return this.argumentTypes; }
    public DataType getReturnType() { return this.returnType; }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define " + returnType + " @(" + argumentTypes.get(0));
        for (int i = 1; i < argumentTypes.size(); i++) {
            sb.append("," + argumentTypes.get(i));
        }
        sb.append(")");
        return sb.toString();
    }
}
