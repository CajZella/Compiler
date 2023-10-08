package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.DataType;
import ir.types.FunctionType;
import ir.types.IntegerType;
import ir.types.VoidType;

public class FuncDef extends AstNode {
    private FuncType funcType;
    private Token ident;
    private FuncFParams funcFParams = null;
    private Block block;
    public FuncDef() {
        super(GrammarType.FuncDef);
    }
    public void addFuncSymbol(SymbolTable symbolTable) {
        if (symbolTable.checkSymbolWhenDecl(ident)) {
            ErrorLog.addError(ErrorType.DUPLICATE_IDENFR, ident.getLine());
        } else {
            DataType returnType = funcType.isVoid() ? new VoidType() : new IntegerType(32);
            FunctionType functionType = new FunctionType(funcFParams.getTypes(), returnType);
            symbolTable.addSymbol(new Symbol(ident.getValue(), false, functionType, ident.getLine()));
        }
    }
    public void setFuncType(FuncType funcType) {
        this.funcType = funcType;
    }
    public void setIdent(Token ident) {
        this.ident = ident;
    }
    public void setFuncFParams(FuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }
    public void setBlock(Block block) {
        this.block = block;
    }
    public FuncType getFuncType() {
        return funcType;
    }
    public Token getIdent() {
        return ident;
    }
    public boolean hasFuncFParams() {
        return funcFParams != null;
    }
    public FuncFParams getFuncFParams() {
        return funcFParams;
    }
    public Block getBlock() {
        return block;
    }
}
