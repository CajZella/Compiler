package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.symbolTable.SymbolTable;

public class MulExp extends AstNode {
    public MulExp() {
        super(GrammarType.MulExp);
    }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isUnaryExp()) { node.checkSema(symbolTable); }
        }
    }
    public int getOpResult() {
        int result = 1;
        for (int i = 0; i < elements.size(); i++) {
            if (i == 0) {
                result *= ((UnaryExp)elements.get(i)).getOpResult();
            } else if (i % 2 == 0) {
                switch (((Token) elements.get(i - 1)).getType()) {
                    case MULT -> result *= ((UnaryExp) elements.get(i)).getOpResult();
                    case DIV -> result /= ((UnaryExp) elements.get(i)).getOpResult();
                    case MOD -> result %= ((UnaryExp) elements.get(i)).getOpResult();
                    default -> {}
                }
            }
        }
        return result;
    }
}
