package frontend.parser.astNode;

import ir.types.DataType;

import java.util.ArrayList;

public abstract class AstNode {
    protected GrammarType grammarType;
    protected ArrayList<AstNode> elements;
    public AstNode(GrammarType grammarType) {
        this.grammarType = grammarType;
        elements = new ArrayList<>();
    }
    public DataType getDataType() {return elements.get(0).getDataType();}
    public void addElement(AstNode element) {
        elements.add(element);
    }

    public AstNode get(int index) {
        return elements.get(index);
    }
    public AstNode getLast() { return elements.get(elements.size() - 1); }

    public boolean isStmt() { return grammarType == GrammarType.Stmt; }


    public String toString() {
        return String.format("<%s>", grammarType.toString());
    }
}
