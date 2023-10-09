package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.SymbolTable;

public class StmtGetint extends Stmt {
    private LVal lVal = null;
    public StmtGetint() {
        super(StmtType.StmtGetint);
    }
    public void setLVal(LVal lVal) {
        this.lVal = lVal;
    }
    public LVal getLVal() {
        return lVal;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (lVal != null) {
            lVal.checkSema(symbolTable);
        }
        if (lVal.getSymbol().isConst()) {
            ErrorLog.addError(ErrorType.CONST_ASSIGNMENT, lVal.getIdent().getLine());
        }
    }
}
