package ir;

import ir.types.Type;

import java.util.LinkedList;

public class Value {
    public enum ValueType {
        ArgumentVal,
        BasicBlockVal,
        FunctionVal,
        InstructionVal,
        GlobalVariableVal,
        ConstantVal,
        ConstantIntVal,
        ConstantArrayVal,
    }
    private ValueType valueTy;
    private String name = null; // 可能为空字符串
    private LinkedList<Use> useList; // def-use，使用某个Value的User列表
    protected Type type; // LLVM value是有类型的

    public Type getType() { return this.type; }
    public LinkedList<Use> getUses() { return this.useList; }
    public int getUseSize() { return this.useList.size(); }
    public boolean isUseEmpty() { return this.useList.isEmpty(); }
    public Use getUserBack() { return this.useList.getLast(); }
    public boolean hasName() { return null != this.name; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public ValueType getValueTy() { return this.valueTy; }
    /*
        traverses the use list of a Value changing all Users of the current value to refer to “V” instead
     */
    public void replaceAllUsesWith(Value V) {
        for (Use use : this.useList) {
            use.setVal(V);
        }
        useList.clear();
    }
    public void addUse(Use use) { this.useList.add(use); }

}
