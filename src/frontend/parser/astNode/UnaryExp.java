package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.DataType;
import ir.types.FunctionType;

public class UnaryExp extends AstNode {
    private PrimaryExp primaryExp;
    private Token ident;
    private FuncRParams funcRParams;
    private DataType funcReturnType;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;
    private int type = 0;
    public UnaryExp() {
        super(GrammarType.UnaryExp);
    }
    public void setPrimaryExp(PrimaryExp primaryExp) { this.primaryExp = primaryExp; }
    public boolean checkIdent(Token ident, SymbolTable symbolTable) {
        Symbol symbol = symbolTable.getSymbol(ident.getValue());
        if (symbol == null) {
            ErrorLog.addError(ErrorType.UNDEFINED_IDENFR, ident.getLine());
            return false;
        } else {
            FunctionType functionType = (FunctionType) symbol.getType();
            funcReturnType = functionType.getReturnType();
            return true;
        }
    }
    @Override
    public DataType getDataType() {
        if (isPrimaryExpType()) {
            return primaryExp.getDataType();
        } else if (isCallFuncType()) {
            return funcReturnType;
        } else {
            return unaryExp.getDataType();
        }
    }
    public void setIdent(Token ident) { this.ident = ident; }
    public void setFuncRParams(FuncRParams funcRParams) { this.funcRParams = funcRParams; }
    public void setUnaryOp(UnaryOp unaryOp) { this.unaryOp = unaryOp; }
    public void setUnaryExp(UnaryExp unaryExp) { this.unaryExp = unaryExp; }
    public PrimaryExp getPrimaryExp() { return primaryExp; }
    public Token getIdent() { return ident; }
    public FuncRParams getFuncRParams() { return funcRParams; }
    public UnaryOp getUnaryOp() { return unaryOp; }
    public UnaryExp getUnaryExp() { return unaryExp; }
    public void setPrimaryExpType() { this.type = 1; }
    public void setCallFunc() { this.type = 2; }
    public void setUnaryType() { this.type = 3; }
    public boolean isPrimaryExpType() { return type == 1; }
    public boolean isCallFuncType() { return type == 2; }
    public boolean isUnaryType() { return type == 3; }
}
