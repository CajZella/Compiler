package ir;

import ir.types.Type;
import util.MyLinkedList;
import util.MyLinkedNode;

// 指令、操作数、常量等的基类
public abstract class Value extends MyLinkedNode {
    public enum ValueType {
        Argument,
        BasicBlock,
        Function,
        GlobalVariable,
        ConstantInt,
        ConstantArray,
        ConstantStr,
        add,
        sub,
        mul,
        sdiv,
        srem,
        icmp,
        and,
        or,
        call,
        alloca,
        load,
        store,
        getelementptr,
        phi,
        zext,
        trunc,
        br,
        ret,
        module,
    }
    protected ValueType valueTy;
    protected String name = null; // 可能为空字符串
    protected static int num = 0; // 用于生成唯一的名字
    private MyLinkedList<Use> useList = new MyLinkedList<>(); // def-use，使用某个Value的User列表
    protected Type type; // LLVM value是有类型的
    public Value(ValueType valueTy, String name, Type type) {
        this.valueTy = valueTy;
        this.type = type;
        this.name = name;
    }
    public void clearNum() { num = 0; }
    public Type getType() { return this.type; }
    public boolean hasName() { return null != this.name; }
    public String getName() { return this.name; }
    public void addUse(Use use) { useList.insertAtTail(use); }
    public void removeUser(Use use) { useList.remove(use); }
    /*
        traverses the use list of a Value changing all Users of the current value to refer to “V” instead
     */
    public void replaceAllUsesWith(Value V) {
        for (Use use : this.useList) {
            use.setVal(V);
        }
        V.useList.addAll(this.useList);
        this.useList.clear();
    }
    public boolean isConstantInt() { return this.valueTy == ValueType.ConstantInt; }
    public boolean isConstantArray() { return this.valueTy == ValueType.ConstantArray; }
}
