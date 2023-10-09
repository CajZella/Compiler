package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

public class Block extends AstNode {
    private int rbraceLine;
    private ArrayList<BlockItem> blockItems;
    public Block() {
        super(GrammarType.Block);
        blockItems = new ArrayList<>();
    }
    public void addBlockItem(BlockItem blockItem) { blockItems.add(blockItem); }
    public void setRbraceLine(int rbraceLine) { this.rbraceLine = rbraceLine; }
    public int getRbraceLine() { return this.rbraceLine; }
    public ArrayList<BlockItem> getBlockItems() { return this.blockItems; }
    public void checkSema(SymbolTable symbolTable) {
        for (BlockItem blockItem : blockItems) {
            blockItem.setFuncType(funcType);
            blockItem.setInLoop(isInLoop);
            blockItem.checkSema(symbolTable);
        }
    }
}
