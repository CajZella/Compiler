package ir.constants;

import ir.types.Type;

import java.util.ArrayList;

/*
    an integer constant of any width
    @a = dso_local global i32 0 // 若全局变量x未被初始化
    @b = dso_local global i32 1 // 若全局变量x被初始化为1
 */
public class ConstantInt extends Constant {
    private int val;
    public ConstantInt(Type type, int val) {
        super(ValueType.ConstantInt, Integer.toString(val), type);
        this.val = val;
    }
    public void setVal(int val) { this.val = val; }
    public int getVal() { return this.val; }
    public Constant getElement(ArrayList<Integer> idxs, Type type) {
        Constant nowEle = this;
        int now = 0;
        while (now < idxs.get(0)) {
            nowEle = (Constant) nowEle.getNext();
            now++;
        }
        return nowEle;
    }
    @Override
    public String toString() {
        return String.format("%d", this.val);
    }
}
