package ir;

import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.VoidType;
import ir.valueSymtab.ValueSymtab;

import java.util.ArrayList;
import java.util.LinkedList;

public class Module extends Value { // todo: 或许module继承value不合理，这里是为了CompUnit调用visit能和其他node统一

    private final LinkedList<Function> functions;
    private final LinkedList<GlobalVariable> globalVariables;
    private final ValueSymtab symtab;
    public Module() {
        super(ValueType.module, null, null);
        functions = new LinkedList<>();
        globalVariables = new LinkedList<>();
        symtab = new ValueSymtab(null);
        this.addFunction(new Function("getint", new FunctionType(new ArrayList<>(), new IntegerType(32)), true));
        this.addFunction(new Function("putint", new FunctionType(new ArrayList<>() {{
            add(new IntegerType(32));
        }}, new VoidType()), true));
        this.addFunction(new Function("putstr", new FunctionType(new ArrayList<>() {{
            add(new PointerType(new IntegerType(8)));
        }}, new VoidType()), true));
        this.addFunction(new Function("putch", new FunctionType(new ArrayList<>() {{
            add(new IntegerType(32));
        }}, new VoidType()), true));

    }
    public void addFunction(Function function) {
        functions.add(function);
        function.setSymtab(new ValueSymtab(symtab));
        //symtab.insertValueSym(function.getName(), function);
    }
    public void addGlobalVariable(GlobalVariable globalVariable) {
        globalVariables.add(globalVariable);
        symtab.insertValueSym(globalVariable.getName(), globalVariable);
    }
    public ValueSymtab getSymtab() { return symtab; }
    public LinkedList<Function> getFunctions() { return functions; }
    public LinkedList<GlobalVariable> getGlobalVariables() { return globalVariables; }
    public Function getFunction(String name) {
        for (Function function : functions) {
            if (function.getName().equals("@" + name)) {
                return function;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Function function : functions)
            if (function.isBuiltin())
                builder.append(function);
        for (GlobalVariable globalVariable : globalVariables) {
            builder.append(globalVariable + "\n");
        }
        for (Function function : functions)
            if (!function.isBuiltin())
                builder.append(function);
        return builder.toString();
    }
}
