package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Initial;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.ArrayType;
import ir.types.IntegerType;
import ir.types.Type;

import java.util.ArrayList;

public class VarDef extends AstNode {
    private Token ident;
    private ArrayList<ConstExp> constExps;
    private InitVal initVal = null;
    public VarDef(){
        super(GrammarType.VarDef);
        constExps = new ArrayList<>();
    }
    public void setIdent(Token ident) {
        this.ident = ident;
    }
    public void addConstExp(ConstExp constExp) {
        constExps.add(constExp);
    }
    public void setInitVal(InitVal initVal) {
        this.initVal = initVal;
    }
    public Token getIdent() {
        return ident;
    }
    public boolean hasConstExps() {
        return !constExps.isEmpty();
    }
    public ArrayList<ConstExp> getConstExps() {
        return constExps;
    }
    public boolean hasInitVal() {
        return initVal != null;
    }
    public InitVal getInitVal() {
        return initVal;
    }
    public void addSymbolTable(SymbolTable symbolTable, BType bType) {

    }
    public void checkSema(SymbolTable symbolTable) {
        // step1. check constExps
        if (hasConstExps()) {
            for (ConstExp constExp : constExps) {
                constExp.checkSema(symbolTable);
            }
        }
        // step2. check initVal
        if (hasInitVal()) {
            initVal.checkSema(symbolTable);
        }
        // step3. check ident
        if (!symbolTable.checkSymbolWhenDecl(ident)) {
            // step4. add symbol
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
            Symbol symbol = new Symbol(ident.getValue(), false, type, ident.getLine());
            symbolTable.addSymbol(symbol);
        }
    }
}
