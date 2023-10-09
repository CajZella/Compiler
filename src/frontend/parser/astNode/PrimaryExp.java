package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;
import ir.types.DataType;

public class PrimaryExp extends AstNode {
    public PrimaryExp() {
        super(GrammarType.PrimaryExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        elements.get(0).checkSema(symbolTable);
    }
}
