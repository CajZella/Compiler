package frontend.parser.astNode;

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
}
