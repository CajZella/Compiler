package frontend.parser.astNode;

import frontend.lexer.Token;
import frontend.lexer.WordType;
import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

/* AddExp -> MulExp ('+' | '-') MulExp */
public class AddExp extends AstNode {
//    private ArrayList<MulExp> mulExps;
//    private ArrayList<Token> operations;
    public AddExp() {
        super(GrammarType.AddExp);
//        mulExps = new ArrayList<>();
//        operations = new ArrayList<>();
    }
//    public void addMulExp(MulExp mulExp) {
//        mulExps.add(mulExp);
//    }
//    public void addOperation(Token token) {
//        assert token.getType() == WordType.PLUS || token.getType() == WordType.MINU;
//        operations.add(token);
//    }
//    public ArrayList<MulExp> getMulExps() { return this.mulExps; }
//    public ArrayList<Token> getOperations() { return this.operations; }
    public void checkSema(SymbolTable symbolTable) {
        for (AstNode node : elements) {
            if (node.isMulExp()) { ((MulExp)node).checkSema(symbolTable); }
        }
    }
}
