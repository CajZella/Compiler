package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;
import ir.constants.Constant;
import ir.constants.ConstantArray;
import ir.constants.ConstantInt;
import ir.types.ArrayType;
import ir.types.Type;

import java.util.ArrayList;

public class InitVal extends AstNode {
    private Exp exp = null;
    private ArrayList<InitVal> initVals;
    public InitVal() {
        super(GrammarType.InitVal);
        initVals = new ArrayList<>();
    }
    public void addInitVal(InitVal initVal) {
        initVals.add(initVal);
    }
    public void setExp(Exp exp) {
        this.exp = exp;
    }
    public boolean isExp() {
        return exp != null;
    }
    public Exp getExp() {
        return exp;
    }
    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }
    public void checkSema(SymbolTable symbolTable) {
        if (isExp()) {
            exp.checkSema(symbolTable);
        } else {
            for (InitVal initVal : initVals) {
                initVal.setGlobal(isGlobal);
                initVal.checkSema(symbolTable);
            }
        }
    }
    public Constant getConstInit(Type type) {
        if (isExp() && isGlobal) {
            return new ConstantInt(type, exp.getOpResult());
        } else if (!isExp()) {
            ConstantArray constantArray = new ConstantArray(type);
            for (InitVal initVal : initVals) {
                if (initVal.isExp())
                    constantArray.addVal(new ConstantInt(((ArrayType)type).getElementType(),
                            initVal.getExp().getOpResult()));
                else
                    constantArray.addVal(initVal.getConstInit(((ArrayType)type).getElementType()));
            }
            return constantArray;
        }
        return null;
    }
}
