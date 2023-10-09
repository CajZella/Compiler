package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class ConstExp extends AstNode {
//    private AddExp addExp;
    public ConstExp(){
        super(GrammarType.ConstExp);
    }
    public int getResult() { return 0; } // todo
    public void checkSema(SymbolTable symbolTable) {
        ((AddExp)elements.get(0)).checkSema(symbolTable);
    }
//    public void setAddExp(AddExp addExp) { this.addExp = addExp; }
//    public AddExp getAddExp() { return addExp; }
}
