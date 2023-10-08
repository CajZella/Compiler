package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;
import ir.types.DataType;
import ir.types.Type;

import java.util.ArrayList;

public class FuncFParams extends AstNode {
    private ArrayList<FuncFParam> funcFParams;
    public FuncFParams() {
        super(GrammarType.FuncFParams);
        funcFParams = new ArrayList<>();
    }
    public void addFuncFParam(FuncFParam funcFParam) {
        funcFParams.add(funcFParam);
    }
    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }
    public ArrayList<DataType> getTypes() {
        ArrayList<DataType> types = new ArrayList<>();
        for (FuncFParam funcFParam : funcFParams) {
            types.add(funcFParam.getType());
        }
        return types;
    }
    public void addToSymbolTable(SymbolTable symbolTable) {
        for (FuncFParam funcFParam : funcFParams) {
            funcFParam.addSymbol(symbolTable);
        }
    }
}
