package frontend.parser.astNode;

import ir.types.DataType;
import ir.types.IntegerType;

public class Number extends AstNode {
    private DataType dataType = new IntegerType(32);
    public Number() {
        super(GrammarType.Number);
    }
    @Override
    public DataType getDataType() { return dataType; }
}
