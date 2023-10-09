package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.ArrayType;
import ir.types.DataType;
import ir.types.IntegerType;
import ir.types.Type;

import java.util.ArrayList;

public class ConstDef extends AstNode {
    private Token ident;
    private ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;
    public ConstDef() {
        super(GrammarType.ConstDef);
        constExps = new ArrayList<>();
    }
    public void setIdent(Token ident) { this.ident = ident; }
    public void addConstExp(ConstExp constExp) { constExps.add(constExp); }
    public void setConstInitVal(ConstInitVal constInitVal) { this.constInitVal = constInitVal; }
    public Token getIdent() { return ident; }
    public boolean hasConstExps() { return !constExps.isEmpty(); }
    public ArrayList<ConstExp> getConstExps() { return constExps; }
    public ConstInitVal getConstInitVal() { return constInitVal; }
    public void checkSema(SymbolTable symbolTable) {
        // step1. check constExps
        if (hasConstExps()) {
            for (ConstExp constExp : constExps) {
                constExp.checkSema(symbolTable);
            }
        }
        // step2. check constInitVal
        constInitVal.checkSema(symbolTable);
        // step3. check ident
        if (!symbolTable.checkSymbolWhenDecl(ident)) {
            // step4. add into symbol table
            Type type;
            if (hasConstExps()) { // ArrayType
                ArrayList<Integer> dims = new ArrayList<>();
                for(ConstExp constExp : constExps) {
                    dims.add(0, constExp.getResult());
                }
                type = new ArrayType(dims, new IntegerType(32));
            } else { // IntegerType
                type = new IntegerType(32);
            }
            Symbol symbol = new Symbol(ident.getValue(), true, type, ident.getLine());
            symbolTable.addSymbol(symbol);
        }
    }
}
