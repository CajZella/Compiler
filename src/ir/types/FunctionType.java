package ir.types;

import java.util.ArrayList;

// include argument types and return type
public class FunctionType extends Type {
    private ArrayList<DataType> argumentTypes;
    private DataType returnType;
    public boolean isIntegerTy(int bitWidth) { return false; }
    public FunctionType(ArrayList<DataType> argumentTypes, DataType returnType) {
        super(TypeID.FunctionTyID);
        if (null == argumentTypes) {
            this.argumentTypes = new ArrayList<>();
        } else { this.argumentTypes = argumentTypes; }
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
    @Override
    public boolean equals(Object object) {
        if (object instanceof FunctionType) {
            FunctionType functionType = (FunctionType) object;
            if (this.argumentTypes.size() != functionType.getArgumentTypes().size()) {
                return false;
            } else {
                for (int i = 0; i < this.argumentTypes.size(); i++) {
                    if (!this.argumentTypes.get(i).equals(functionType.getArgumentTypes().get(i))) {
                        return false;
                    }
                }
                return this.returnType.equals(functionType.getReturnType());
            }
        } else {
            return false;
        }
    }
}
