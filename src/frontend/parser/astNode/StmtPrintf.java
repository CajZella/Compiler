package frontend.parser.astNode;

import frontend.lexer.Token;

import java.util.ArrayList;

public class StmtPrintf extends Stmt {
    private Token formatString;
    private ArrayList<Exp> exps;
    public StmtPrintf() {
        super(StmtType.StmtPrintf);
        exps = new ArrayList<>();
    }
    public void setFormatString(Token formatString) {
        this.formatString = formatString;
    }
    public void addExp(Exp exp) {
        exps.add(exp);
    }
    public Token getFormatString() {
        return formatString;
    }
    public boolean hasExps() {
        return !exps.isEmpty();
    }
    public ArrayList<Exp> getExps() {
        return exps;
    }
}
