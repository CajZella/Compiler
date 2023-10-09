package frontend.symbolTable;

import frontend.parser.astNode.CompUnit;

public class Sema {
    private CompUnit compUnit;
    private SymbolTable symbolTable;

    public Sema(CompUnit compUnit) {
        this.compUnit = compUnit;
        this.symbolTable = new SymbolTable(null);
    }
    public void run() {
        compUnit.checkSema(symbolTable);
    }
}
