package ir;

import ir.constants.Constant;
import ir.types.FunctionType;
import ir.valueSymtab.ValueSymtab;
import util.MyLinkedList;
import java.util.ArrayList;

// keep track of a list of BasicBlocks, formal Arguments and a SymbolTable
public class Function extends Value {
    private MyLinkedList<BasicBlock> blocks;
    private ArrayList<Argument> arguments;
    private ValueSymtab symtab;
    public Function(String name, FunctionType functionType, ValueSymtab symtab) {
        super(ValueType.Function, String.format("@%s", name), functionType);
        this.arguments = new ArrayList<>();
        this.symtab = symtab;
        this.blocks = new MyLinkedList<>();
    }
    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }
    public void addBlock(BasicBlock block) {
        this.blocks.insertAtTail(block);
    }
    public void addSym(String name, Value value) {
        this.symtab.insertValueSym(name, value);
    }
    public boolean isEmpty() { return this.blocks.isEmpty(); }
    public int size() { return this.blocks.size(); }
    public BasicBlock getEntryBlock() { return this.blocks.getHead(); }
    public ArrayList<Argument> getArguments() { return this.arguments; }
    public ValueSymtab getSymtab() { return this.symtab; }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("define dso_local %s @%s(", ((FunctionType)type).getReturnType(), name));
        for (int i = 0; i < this.arguments.size(); i++) {
            Argument argument = this.arguments.get(i);
            builder.append(String.format("%s %s", argument.getType(), argument.getName()));
            if (i != this.arguments.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(") {\n");
        for (BasicBlock block : this.blocks) {
            builder.append(block);
        }
        builder.append("}\n");
        return builder.toString();
    }
}
