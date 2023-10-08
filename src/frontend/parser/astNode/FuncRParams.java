package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.symbolTable.Symbol;
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
    public void checkCallFunc(Symbol funcSymbol, int line) {
        FunctionType functionType = (FunctionType) funcSymbol.getType();
        ArrayList<DataType> formalArgs = functionType.getArgumentTypes();
        if (formalArgs.size() != exps.size()) {
            ErrorLog.addError(ErrorType.FUNC_PARAM_NUMBER_MISMATCHED, line);
        } else {
            boolean flag = true;
            for (int i = 0; i < exps.size(); i++) {
                if (exps.get(i).getDataType() != formalArgs.get(i)) {
                    flag = false;
                }
            }
            if (!flag) { ErrorLog.addError(ErrorType.FUNC_PARAM_TYPE_MISMATCHED, line); }
        }
    }
}
