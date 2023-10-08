package frontend.parser.astNode;

import java.util.ArrayList;

public class Block extends AstNode {
    private ArrayList<BlockItem> blockItems;
    public Block() {
        super(GrammarType.Block);
        blockItems = new ArrayList<>();
    }
    public void addBlockItem(BlockItem blockItem) { blockItems.add(blockItem); }
    public ArrayList<BlockItem> getBlockItems() { return this.blockItems; }
}
