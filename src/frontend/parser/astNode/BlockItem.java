package frontend.parser.astNode;

import java.util.ArrayList;

public class BlockItem extends AstNode {
    public BlockItem() {
        super(GrammarType.BlockItem);
    }
    public boolean isDecl() {
        return elements.get(0) instanceof Decl;
    }
    public boolean isStmt() {
        return elements.get(0) instanceof Stmt;
    }
    public Decl getDecl() {
        return (Decl)elements.get(0);
    }
    public Stmt getStmt() {
        return (Stmt)elements.get(0);
    }
}
