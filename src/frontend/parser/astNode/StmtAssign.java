package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.SymbolTable;

public class StmtAssign extends Stmt {
    private LVal lVal;
    private Exp exp;
    public StmtAssign() {
        super(StmtType.StmtAssign);
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
        if (null != lVal.getSymbol() && lVal.getSymbol().isConst()) {
            ErrorLog.addError(ErrorType.CONST_ASSIGNMENT, lVal.getIdent().getLine());
        }
    }
}
