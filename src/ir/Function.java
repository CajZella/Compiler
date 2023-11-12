package ir;

import ir.constants.Constant;
import ir.types.DataType;
import ir.types.FunctionType;
import ir.valueSymtab.ValueSymtab;
import util.MyLinkedList;
import java.util.ArrayList;
import java.util.Iterator;

// keep track of a list of BasicBlocks, formal Arguments and a SymbolTable
public class Function extends Value {
    private MyLinkedList<BasicBlock> blocks;
    private ArrayList<Argument> arguments;
    private ValueSymtab symtab;
    private boolean isBuiltin;
    public Function(String name, FunctionType functionType, boolean isBuiltin) {
        super(ValueType.Function, String.format("@%s", name), functionType);
        this.arguments = new ArrayList<>();
        this.blocks = new MyLinkedList<>();
        this.isBuiltin = isBuiltin;
    }
    public void addBlock(BasicBlock block) {
        this.blocks.insertAtTail(block);
        this.symtab.insertValueSym(block.getName(), block);
    }
    public MyLinkedList<BasicBlock> getBlocks() { return this.blocks; }
    public boolean isEmpty() { return this.blocks.isEmpty(); }
    public int size() { return this.blocks.size(); }
    public BasicBlock getEntryBlock() { return this.blocks.getHead(); }

    public boolean isMain() { return this.name.equals("@main"); }
    public void setSymtab(ValueSymtab symtab) { this.symtab = symtab; }
    public void addArgument(Argument argument) {
        this.arguments.add(argument);
        this.symtab.insertValueSym(argument.getName(), argument);
    }
    public void addSym(String name, Value value) { this.symtab.insertValueSym(name, value); }
    public ArrayList<Argument> getArguments() { return this.arguments; }
    public ValueSymtab getSymtab() { return this.symtab; }
    public boolean isBuiltin() { return this.isBuiltin; }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!isBuiltin) {
            builder.append(String.format("define dso_local %s %s(", ((FunctionType) type).getReturnType(), name));
            for (int i = 0; i < this.arguments.size(); i++) {
                Argument argument = this.arguments.get(i);
                builder.append(String.format("%s %s", argument.getType(), argument.getName()));
                if (i != this.arguments.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append(") {\n");
            Iterator<BasicBlock> iterator = blocks.iterator();
            while (iterator.hasNext()) {
                builder.append(iterator.next());
            }
            builder.append("}\n");
        } else {
            builder.append(String.format("declare %s %s(", ((FunctionType) type).getReturnType(), name));
            ArrayList<DataType> argumentTypes = ((FunctionType)type).getArgumentTypes();
            for (int i = 0; i < argumentTypes.size(); i++) {
               DataType dataType = argumentTypes.get(i);
                builder.append(String.format("%s", dataType));
            }
            builder.append(")\n");
        }
        return builder.toString();
    }
}
