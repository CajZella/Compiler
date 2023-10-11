package ir;

import ir.instrs.Instr;
import ir.types.LabelType;
import util.MyLinkedList;

import java.util.LinkedList;

// a single entry single exit section of the code
public class BasicBlock extends Value {
    private final MyLinkedList<Instr> instrs;
    private final Function pFunction;
    public BasicBlock(int num, Function parent) {
        super(ValueType.BasicBlock, String.format("%d", num), new LabelType());
        this.pFunction = parent;
        this.instrs = new MyLinkedList<>();
        pFunction.addSym(name, this);
    }
    public Function getParent() { return this.pFunction; }
    public void addInstr(Instr instr) { this.instrs.insertAtTail(instr); }
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
        builder.append(String.format("%s:\n", name));
        for (Instr instr : instrs) {
            builder.append(instr + "\n");
        }
        return builder.toString();
    }
}
