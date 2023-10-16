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
    private Token ident;
    private FuncFParams funcFParams = null;
    private Block block;
    public FuncDef() {
        super(GrammarType.FuncDef);
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
    public void checkSema(SymbolTable symbolTable) {
        // step1. function symbol
        DataType returnType = funcType.isVoid() ? new VoidType() : new IntegerType(32);
        FunctionType functionType = new FunctionType(returnType);
        if (!symbolTable.checkSymbolWhenDecl(ident)) {
            symbolTable.addSymbol(new Symbol(ident.getValue(), false, true, functionType, ident.getLine()));
        }
        // step2. FunFParams check
        SymbolTable childTable = new SymbolTable(symbolTable);
        if (hasFuncFParams()) {
            funcFParams.checkSema(childTable);
            functionType.setArgumentTypes(funcFParams.getTypes());
        }

        // step3. Block check
        block.setFuncType(funcType);
        block.checkSema(childTable);
        // step4. check return stmt
        if (funcType.isInt()) {
            if (block.getBlockItems().isEmpty()) {
                ErrorLog.addError(ErrorType.NON_RETURN_FUNC, block.getRbraceLine());
            } else {
                BlockItem blockItem = block.getBlockItems().get(block.getBlockItems().size() - 1);
                if (blockItem.isStmt()) {
                    Stmt stmt = blockItem.getStmt();
                    if (!stmt.isReturnStmt()) {
                        ErrorLog.addError(ErrorType.NON_RETURN_FUNC, block.getRbraceLine());
                    }
                } else
                    ErrorLog.addError(ErrorType.NON_RETURN_FUNC, block.getRbraceLine());
            }
        } else {
            if (block.getBlockItems().isEmpty()) {
                BlockItem blockItem = new BlockItem();
                blockItem.addElement(new StmtReturn());
                block.addBlockItem(blockItem);
            } else {
                BlockItem blockItem = block.getBlockItems().get(block.getBlockItems().size() - 1);
                if (blockItem.isStmt()) {
                    Stmt stmt = blockItem.getStmt();
                    if (!stmt.isReturnStmt()) {
                        blockItem = new BlockItem();
                        blockItem.addElement(new StmtReturn());
                        block.addBlockItem(blockItem);
                    }
                } else {
                    blockItem = new BlockItem();
                    blockItem.addElement(new StmtReturn());
                    block.addBlockItem(blockItem);
                }
            }
        }
    }
}
