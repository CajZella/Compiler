package ir;

import ir.types.ArrayType;
import ir.types.Type;
import util.MyLinkedList;

import java.util.ArrayList;

// user 使用的 value list
public abstract class User extends Value {
    protected ArrayList<Use> operands = new ArrayList<Use>();

    public User(ValueType valueTy, String name, Type type, Value... operands) {
        super(valueTy, name, type);
        for (Value operand : operands) {
            use(operand);
        }
    }
    public User(String name) { super(name); }
    public void use(Value value) {
        Use use = new Use(this, value);
        this.operands.add(use);
        value.addUser(use);
    }
    public ArrayList<Value> getOperands() {
        ArrayList<Value> values = new ArrayList<Value>();
        for (Use use : this.operands) {
            values.add(use.getVal());
        }
        return values;
    }
    public boolean isOperandsEmpty() { return this.operands.isEmpty(); }
    public Value getOperand(int index) { return this.operands.get(index).getVal(); }
    public int operandsSize() { return this.operands.size(); }
    public void dropAllReferences() { // 同时删除this use 的 value关系
        for (Use use : this.operands) {
            use.getVal().removeUser(use);
        }
        this.operands.clear();
    }
    public void replaceUsesOfWith(Value from, Value to) { // 原来use from，改为use to
        for (Use use : this.operands) {
            if (use.getVal() == from) {
                from.getUseList().remove(use);
                use.setVal(to);
                to.addUser(use);
            }
        }
    }
    public void replaceAllUses(Value... operands) { // use 的 value全部更新
        this.dropAllReferences();
        this.operands.clear();
        for (Value operand : operands) {
            use(operand);
        }
    }
    public void replaceAllUses(ArrayList<Value> operands) { // use 的 value全部更新
        this.dropAllReferences();
        this.operands.clear();
        for (Value operand : operands) {
            use(operand);
        }
    }
    public void removeOperand(int index) {
        Use use = operands.get(index);
        operands.remove(use);
        use.getVal().getUseList().remove(use);
    }
    public ArrayList<Use> getUserUses() { return this.operands; }
    public void replaceUsesOfWith(int idx, Value to) { // use operands[idx] 改为 use to
        this.operands.get(idx).getVal().getUseList().remove(this.operands.get(idx));
        Use use = new Use(this, to);
        this.operands.set(idx, use);
        to.addUser(use);
    }
}
