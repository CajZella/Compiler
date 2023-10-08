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

public class MainFuncDef extends AstNode {
    private Token ident;
    private Block block;
    public MainFuncDef() {
        super(GrammarType.MainFuncDef);
    }
    public void setBlock(Block block) {
        this.block = block;
    }
    public void setIdent(Token ident) {
        this.ident = ident;
    }
    public Token getIdent() {
        return ident;
    }
    public void addFuncSymbol(SymbolTable symbolTable) {
        if (symbolTable.checkSymbolWhenDecl(ident)) {
            ErrorLog.addError(ErrorType.DUPLICATE_IDENFR, ident.getLine());
        } else {
            DataType returnType = new IntegerType(32);
            FunctionType functionType = new FunctionType(null, returnType);
            symbolTable.addSymbol(new Symbol(ident.getValue(), false, functionType, ident.getLine()));
        }
    }
    public Block getBlock() {
        return block;
    }
}
