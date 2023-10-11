package ir;

import util.MyLinkedNode;

// use和value一条边的关系
public class Use extends MyLinkedNode {
    private final User user;
    private Value val;
    public Use(User u, Value v) {
        this.user = u;
        this.val = v;
    }
    public User getUser() { return this.user; }
    public Value getVal() { return this.val; }
    public void setVal(Value val) { this.val = val; }
    @Override
    public Use clone() {
        return new Use(user, val);
    }
}
