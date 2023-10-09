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
// int a: i32
// int a[]: i32*
// int a[][5]: [2 x i32]*
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
            for (int i = constExps.size() - 1; i > 0; i--) {
                type = new ArrayType(type, constExps.get(i).getResult());
            }
            type = new PointerType(type);
        }
        return (DataType) type;
    }
    public void checkSema(SymbolTable symbolTable) {
        // step1. check constExps
        for (int i = 1; i < constExps.size(); i++) {
            constExps.get(i).checkSema(symbolTable);
        }
        // step2. check ident
        if (!symbolTable.checkSymbolWhenDecl(ident)) { // step3. add symbol
            symbolTable.addSymbol(new Symbol(ident.getValue(), false, getType(), ident.getLine()));
        }
    }
}
