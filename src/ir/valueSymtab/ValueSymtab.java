package ir.valueSymtab;

import ir.Value;
import util.MyLinkedList;
import util.MyLinkedNode;

import java.util.HashMap;
// 目前没用
public class ValueSymtab extends MyLinkedNode {
    private final HashMap<String, Value> symtab;
    private ValueSymtab parent;
    private MyLinkedList<ValueSymtab> childTab;
    public ValueSymtab(ValueSymtab parent) {
        this.symtab = new HashMap<>();
        this.parent = parent;
        this.childTab = new MyLinkedList<>();
        if (null != parent) parent.addChild(this);
    }
    public void addChild(ValueSymtab child) { childTab.insertAtTail(child); }
    public Value lookup(String name) {
        ValueSymtab p = this;
        while (null != p) {
            if (p.symtab.containsKey(name))
                return p.symtab.get(name);
            else
                p = p.parent;
        }
        return null;
    }
    public void reinsertValue(String name, Value value) {
        symtab.put(name, value);
    }
    public void removeValueSym(String name) {
        symtab.remove(name);
    }
    public void insertValueSym(String name, Value value) {
        if (!symtab.containsKey(name))
            symtab.put(name, value);
    }
}
