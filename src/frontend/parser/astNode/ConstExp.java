package frontend.parser.astNode;

public class ConstExp extends AstNode {
//    private AddExp addExp;
    public ConstExp(){
        super(GrammarType.ConstExp);
    }
    public int getResult() { return 0; } // todo
//    public void setAddExp(AddExp addExp) { this.addExp = addExp; }
//    public AddExp getAddExp() { return addExp; }
}
