package ir;

import ir.instrs.Instr;

import java.util.LinkedList;

// a single entry single exit section of the code
public class BasicBlock extends Value {
    private LinkedList<Instr> instrs;
    private Function pFunction;

    public LinkedList<Instr> getInstrs() { return this.instrs; }
    public Function getParent() { return this.pFunction; }
    public Instr getTerminator() { return this.instrs.getLast(); }
}
