package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;
import ir.types.DataType;

public class PrimaryExp extends AstNode {
    public PrimaryExp() {
        super(GrammarType.PrimaryExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        elements.get(0).checkSema(symbolTable);
    }
    public boolean isExp() { return elements.get(0).isExp(); }
    public boolean isLVal() { return elements.get(0).isLVal(); }
    public boolean isNumber() { return elements.get(0).isNumber(); }
    public Exp getExp() { return (Exp)elements.get(0); }
    public LVal getLVal() { return (LVal)elements.get(0); }
    public Number getNumber() { return (Number)elements.get(0); }
    public int getOpResult() {
        if (isExp()) {
            return getExp().getOpResult();
        } else if (isLVal()) {
            return getLVal().getOpResult();
        } else {
            return getNumber().getOpResult();
        }
    }
}
