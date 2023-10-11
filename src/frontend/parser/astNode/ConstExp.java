package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class ConstExp extends AstNode {
//    private AddExp addExp;
    public ConstExp(){
        super(GrammarType.ConstExp);
    }
    public AddExp getExp() { return (AddExp)elements.get(0); }
    public int getOpResult() { return getExp().getOpResult(); }
    public void checkSema(SymbolTable symbolTable) { getExp().checkSema(symbolTable); }
//    public void setAddExp(AddExp addExp) { this.addExp = addExp; }
//    public AddExp getAddExp() { return addExp; }
}
