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
    public void use(Value value) {
        Use use = new Use(this, value);
        this.operands.add(use);
        value.addUse(use);
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
    public void setOperand(int index, Value value) {
        Use use = this.operands.get(index);
        use.setVal(value);
    }
    public int operandsSize() { return this.operands.size(); }
    public int getNumOperands() { return this.operands.size(); }
    public void dropAllReferences() {
        for (Use use : this.operands) {
            use.getVal().removeUser(use);
        }
        this.operands.clear();
    }
    public void replaceUsesOfWith(Value from, Value to) {
        for (Use use : this.operands) {
            if (use.getVal() == from) {
                use.setVal(to);
            }
        }
    }
}
