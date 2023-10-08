package frontend.parser.astNode;

public abstract class Stmt extends AstNode {
    public enum StmtType {
        StmtBlock,
        StmtIf,
        StmtFor,
        StmtAssign,
        StmtReturn,
        StmtExp,
        StmtBreak,
        StmtContinue,
        StmtGetint,
        StmtPrintf,
        StmtWhile,
    }
    protected StmtType stmtType;
    public Stmt(StmtType stmtType) {
        super(GrammarType.Stmt);
        this.stmtType = stmtType;
    }
    public boolean isReturnStmt() { return stmtType == StmtType.StmtReturn; }
}
