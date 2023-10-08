package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.ArrayType;
import ir.types.DataType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.Type;

import java.util.ArrayList;

public class FuncFParam extends AstNode {
    private BType bType;
    private Token ident;
    private ArrayList<ConstExp> constExps;
    public FuncFParam() {
        super(GrammarType.FuncFParam);
        constExps = new ArrayList<>();
    }
    public void setBType(BType bType) { this.bType = bType; }
    public void setIdent(Token ident) { this.ident = ident; }
    public void addConstExp(ConstExp constExp) { constExps.add(constExp); }
    public BType getBType() { return bType; }
    public Token getIdent() { return ident; }
    public boolean hasConstExps() { return !constExps.isEmpty(); }
    public ArrayList<ConstExp> getConstExps() { return constExps; }
    public DataType getType() {
        Type type;
        if (!hasConstExps()) {
            type = new IntegerType(32);
        } else {
            type = new IntegerType(32);
            for (int i = constExps.size() - 2; i >= 0; i--) {
                type = new ArrayType(type, constExps.get(i).getResult());
            }
            type = new PointerType(type);
        }
        return (DataType) type;
    }
    public void addSymbol(SymbolTable symbolTable) {
        if (symbolTable.checkSymbolWhenDecl(ident)) {
            ErrorLog.addError(ErrorType.DUPLICATE_IDENFR, ident.getLine());
        } else {
            symbolTable.addSymbol(new Symbol(ident.getValue(), false, getType(), ident.getLine()));
        }
    }
}
