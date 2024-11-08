package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.lexer.WordType;
import frontend.symbolTable.SymbolTable;

public class FuncType extends AstNode {
    public FuncType() {
        super(GrammarType.FuncType);
    }
    public boolean isVoid() { return ((Token)elements.get(0)).getType() == WordType.VOIDTK; }
    public boolean isInt() { return ((Token)elements.get(0)).getType() == WordType.INTTK; }
    public void checkSema(SymbolTable symbolTable) { return; }
}
