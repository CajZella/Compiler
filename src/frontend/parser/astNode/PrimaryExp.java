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
    public boolean isExpType() { return elements.get(0).isExp(); }
    public boolean isLValType() { return elements.get(0).isLVal(); }
    public boolean isNumberType() { return elements.get(0).isNumber(); }
    public Exp getExp() { return (Exp)elements.get(0); }
    public LVal getLVal() { return (LVal)elements.get(0); }
    public Number getNumber() { return (Number)elements.get(0); }
    public int getOpResult() {
        if (isExpType()) {
            return getExp().getOpResult();
        } else if (isLValType()) {
            return getLVal().getOpResult();
        } else {
            return getNumber().getOpResult();
        }
    }
}
