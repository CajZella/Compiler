package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.SymbolTable;

public class ForStmt extends AstNode {
    private LVal lVal;
    private Exp exp;
    public ForStmt() {
        super(GrammarType.ForStmt);
    }
    public void setLVal(LVal lVal) {
        this.lVal = lVal;
    }
    public void setExp(Exp exp) {
        this.exp = exp;
    }
    public LVal getLVal() {
        return lVal;
    }
    public Exp getExp() {
        return exp;
    }
    public void checkSema(SymbolTable symbolTable) {
        lVal.checkSema(symbolTable);
        exp.checkSema(symbolTable);
        if (lVal.getSymbol().isConst()) {
            ErrorLog.addError(ErrorType.CONST_ASSIGNMENT, lVal.getIdent().getLine());
        }
    }
}
