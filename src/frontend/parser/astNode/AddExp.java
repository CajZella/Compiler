package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.lexer.WordType;
import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

/* AddExp -> MulExp ('+' | '-') MulExp */
public class AddExp extends AstNode {
//    private ArrayList<MulExp> mulExps;
//    private ArrayList<Token> operations;
    public AddExp() { super(GrammarType.AddExp); }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isMulExp()) { ((MulExp)node).checkSema(symbolTable); }
        }
    }
    public int getOpResult() {
        int result = 0;
        for (int i = 0; i < elements.size(); i++) {
            if (i == 0) {
                result += ((MulExp)elements.get(i)).getOpResult();
            } else if (i % 2 == 0) {
                if (((Token)elements.get(i-1)).getType() == WordType.PLUS)
                    result += ((MulExp)elements.get(i)).getOpResult();
                else
                    result -= ((MulExp)elements.get(i)).getOpResult();
            }
        }
        return result;
    }
}
