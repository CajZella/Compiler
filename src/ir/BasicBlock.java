package ir;

import ir.instrs.Instr;
import ir.types.LabelType;
import util.MyLinkedList;

import java.util.Iterator;
import java.util.LinkedList;

// a single entry single exit section of the code
public class BasicBlock extends Value {
    private final MyLinkedList<Instr> instrs;
    private final Function pFunction;
    public BasicBlock(Function parent) {
        super(ValueType.BasicBlock, "%b" + num++, new LabelType());
        this.pFunction = parent;
        this.pFunction.addBlock(this);
        this.instrs = new MyLinkedList<>();
    }
    public Function getParent() { return this.pFunction; }
    public void addInstr(Instr instr) {
        this.instrs.insertAtTail(instr);
        if (instr.hasName())
            pFunction.addSym(instr.getName(), instr);
    }
    public Instr getTerminator() { return this.instrs.getTail(); }
    public Instr getEntryInstr() { return this.instrs.getHead(); }
    public boolean isEmpty() { return this.instrs.isEmpty(); }
    public int size() { return this.instrs.size(); }
    public void dropAllReferences() { //todo
        for (Instr instr : instrs) {
            instr.dropAllReferences();
        }
    }
    @Override
    public String toString() {
        StringBuilder builder =  new StringBuilder();
        builder.append(String.format("%s:\n", name.substring(1)));
        Iterator<Instr> iterator = instrs.iterator();
        while(iterator.hasNext()) {
            Instr instr = iterator.next();
            builder.append("  " + instr.toString() + "\n");
        }
        return builder.toString();
    }
}
