package ir;

import java.util.LinkedList;

public class User extends Value {
    private LinkedList<Value> operands; // use-def 被User使用的Value列表

    public Value getOperand(int i) { return operands.get(i); }
    public LinkedList<Value> getOperands() { return this.operands; }
}
