package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.DataType;
import ir.types.FunctionType;
import ir.types.IntegerType;

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

    public void checkSema(SymbolTable symbolTable) {
        // step1. function symbol
        if (!symbolTable.checkSymbolWhenDecl(ident)) {
            DataType returnType = new IntegerType(32);
            FunctionType functionType = new FunctionType(null, returnType);
            symbolTable.addSymbol(new Symbol(ident.getValue(), false, functionType, ident.getLine()));
        }
        // step2. build child symbol table
        SymbolTable childTable = new SymbolTable(symbolTable);
        // step3. Block check
        block.setFuncType(funcType);
        block.checkSema(childTable);
        // step4. check return stmt
        if (block.getBlockItems().isEmpty()) {
            ErrorLog.addError(ErrorType.NON_RETURN_FUNC, block.getRbraceLine());
        } else {
            BlockItem blockItem = block.getBlockItems().get(block.getBlockItems().size() - 1);
            if (blockItem.isStmt()) {
                Stmt stmt = blockItem.getStmt();
                if (!stmt.isReturnStmt()) {
                    ErrorLog.addError(ErrorType.NON_RETURN_FUNC, block.getRbraceLine());
                }
            }
        }
    }
    public Block getBlock() {
        return block;
    }
}
