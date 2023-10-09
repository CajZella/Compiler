package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

public class StmtBlock extends Stmt {
    private Block block;
    public StmtBlock() {
        super(StmtType.StmtBlock);
    }
    public void setBlock(Block block) {
        this.block = block;
    }
    public Block getBlock() {
        return block;
    }
    public void checkSema(SymbolTable symbolTable) {
        SymbolTable childTable = new SymbolTable(symbolTable);
        block.setFuncType(funcType);
        block.setInLoop(isInLoop);
        block.checkSema(childTable);
    }
}
