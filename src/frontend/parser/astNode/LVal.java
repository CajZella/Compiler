package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.ArrayType;
import ir.types.DataType;
import ir.types.PointerType;
import ir.types.Type;

import java.util.ArrayList;

public class LVal extends AstNode {
    private Token ident;
    private ArrayList<Exp> exps;
    private Type identType;
    public LVal() {
        super(GrammarType.LVal);
        exps = new ArrayList<>();
    }
    public boolean checkIdent(Token ident, SymbolTable symbolTable) {
        Symbol symbol = symbolTable.getSymbol(ident.getValue());
        if (symbol == null) {
            ErrorLog.addError(ErrorType.UNDEFINED_IDENFR, ident.getLine());
            return false;
        } else {
            identType = symbol.getType();
            return true;
        }
    }
    @Override
    public DataType getDataType() {
        if (identType.isArrayTy()) {
            Type type = identType;
            for (int i = 0; i < exps.size(); i++) {
                type = ((ArrayType)identType).getElementType();
            }
            return new PointerType(type);
        } else { return (DataType) identType; }
    }
    public void setIdent(Token ident) { this.ident = ident; }
    public void addExp(Exp exp) { exps.add(exp); }
    public Token getIdent() { return ident; }
    public boolean hasExps() { return !exps.isEmpty(); }
    public ArrayList<Exp> getExps() { return exps; }
}
