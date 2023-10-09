package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.types.DataType;
import ir.types.FunctionType;

import java.util.ArrayList;

public class FuncRParams extends AstNode {
    private ArrayList<Exp> exps;
    public FuncRParams() {
        super(GrammarType.FuncRParams);
        exps = new ArrayList<>();
    }
    public void addExp(Exp exp) {
        exps.add(exp);
    }
    public ArrayList<Exp> getExps() {
        return exps;
    }
    public void checkSema(SymbolTable symbolTable) {
        for (Exp exp : exps) {
            exp.checkSema(symbolTable);
        }
    }
}
