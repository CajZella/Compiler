package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.SymbolTable;

public class StmtReturn extends Stmt {
    private int returnLine;
    private Exp exp = null;
    public StmtReturn() {
        super(StmtType.StmtReturn);
    }
    public void setExp(Exp exp) {
        this.exp = exp;
    }
    public void setReturnLine(int returnLine) { this.returnLine = returnLine; }
    public boolean hasExp() {
        return exp != null;
    }
    public Exp getExp() {
        return exp;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (hasExp()) {
            exp.checkSema(symbolTable);
        }
        if (funcType.isVoid() && hasExp()) {
            ErrorLog.addError(ErrorType.RETURN_VALUE_IN_VOID_FUNC, returnLine);
        }
    }
}
