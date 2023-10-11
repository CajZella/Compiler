package ir;

import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.valueSymtab.ValueSymtab;
import java.util.LinkedList;

public class Module {

    private final LinkedList<Function> functions;
    private final LinkedList<GlobalVariable> globalVariables;
    private final ValueSymtab symtab;
    public Module() {
        functions = new LinkedList<>();
        globalVariables = new LinkedList<>();
        symtab = new ValueSymtab(null);
    }
    public void addFunction(Function function) {
        functions.add(function);
        symtab.insertValueSym(function.getName(), function);
    }
    public void addGlobalVariable(GlobalVariable globalVariable) {
        globalVariables.add(globalVariable);
        symtab.insertValueSym(globalVariable.getName(), globalVariable);
    }
    public LinkedList<Function> getFunctions() { return functions; }
    public LinkedList<GlobalVariable> getGlobalVariables() { return globalVariables; }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("declare i32 @getint()\n" +
                "declare void @putint(i32)\n" +
                "declare void @putch(i32)\n" +
                "declare void @putstr(i8*)");
        for (GlobalVariable globalVariable : globalVariables) {
            builder.append(globalVariable);
        }
        for (Function function : functions) {
            builder.append(function);
        }
        return builder.toString();
    }
}
