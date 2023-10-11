package frontend;

import frontend.parser.astNode.CompUnit;
import frontend.symbolTable.SymbolTable;
import ir.Module;

// 中间代码生成
public class Visitor {
    private final SymbolTable symbolTable;
    private final CompUnit compUnit;
    private Module module;
    public Visitor(SymbolTable symbolTable, CompUnit compUnit) {
        this.symbolTable = symbolTable;
        this.compUnit = compUnit;
        this.module = new Module();
    }
    public void visitComUnit() {

    }
}
