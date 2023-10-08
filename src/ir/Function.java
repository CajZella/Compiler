package ir;

import frontend.symbolTable.SymbolTable;
import ir.constants.GlobalValue;
import ir.types.FunctionType;
import ir.types.Type;

import java.util.ArrayList;
import java.util.LinkedList;

// keep track of a list of BasicBlocks, formal Arguments and a SymbolTable
public class Function extends GlobalValue {
    private LinkedList<BasicBlock> blocks;
    private ArrayList<Argument> arguments;
    private FunctionType funcType;
    private Type returnType;
    private SymbolTable symbolTable;

    public ArrayList<Argument> getArguments() { return this.arguments; }

    public Type getReturnType () { return this.returnType; }

    public FunctionType getFuncType() { return this.funcType; }

    public SymbolTable getSymbolTable() { return this.symbolTable; }
}
