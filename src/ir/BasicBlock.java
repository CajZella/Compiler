package ir;

import ir.instrs.Br;
import ir.instrs.Instr;
import ir.types.LabelType;
import util.MyLinkedList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

// a single entry single exit section of the code
public class BasicBlock extends Value {
    private final MyLinkedList<Instr> instrs;
    private final Function pFunction;
    private HashSet<BasicBlock> precBBs = new HashSet<>();
    private HashSet<BasicBlock> succBBs = new HashSet<>();
    private HashSet<BasicBlock> doms = new HashSet<>(); // 被哪些节点支配
    private HashSet<BasicBlock> df = new HashSet<>();
    private HashSet<BasicBlock> idoms = new HashSet<>(); // the blocks that this immediately dominate
    private BasicBlock idom = null; // who immediately dominate this
    public BasicBlock(Function parent) {
        super(ValueType.BasicBlock, "%" + parent.getMipsName() + "_b" + num++, new LabelType());
        this.pFunction = parent;
        this.pFunction.addBlock(this);
        this.instrs = new MyLinkedList<>();
    }
    public void addPrecBBs(BasicBlock precBBs) { this.precBBs.add(precBBs); }
    public void addSuccBBs(BasicBlock succBBs) { this.succBBs.add(succBBs); }
    public HashSet<BasicBlock> getPrecBBs() { return this.precBBs; }
    public HashSet<BasicBlock> getSuccBBs() { return this.succBBs; }
    public void addDom(BasicBlock dom) { this.doms.add(dom); }
    public HashSet<BasicBlock> getDoms() { return this.doms; }
    public void addIdom(BasicBlock idom) { this.idoms.add(idom); }
    public HashSet<BasicBlock> getIdoms() { return this.idoms; }
    public void setIdom(BasicBlock idom) { this.idom = idom; }
    public BasicBlock getIdom() { return this.idom; }
    public void addDF(BasicBlock df) { this.df.add(df); }
    public HashSet<BasicBlock> getDFs() { return this.df; }
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
    public MyLinkedList<Instr> getInstrs() { return this.instrs; }
    public void dropAllReferences() { //todo
        for (Instr instr : instrs) {
            instr.dropAllReferences();
        }
    }
    @Override
    public String toString() {
        StringBuilder builder =  new StringBuilder();
        builder.append(String.format("%s:\n", name.substring(1)));
        for (Instr instr : instrs) {
            builder.append("  " + instr.toString() + "\n");
        }
        return builder.toString();
    }
}
