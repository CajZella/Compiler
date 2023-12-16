package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.Symbol;
import frontend.symbolTable.SymbolTable;
import ir.constants.Constant;
import ir.constants.ConstantArray;
import ir.constants.ConstantInt;
import ir.types.ArrayType;
import ir.types.DataType;
import ir.types.IntegerType;
import ir.types.PointerType;
import ir.types.Type;
import java.util.ArrayList;

/*
    int a[2][2];
    a: [2 x i32]*
    a[0]: i32*
    a[0][0]: i32
 */
public class LVal extends AstNode {
    private Token ident;
    private ArrayList<Exp> exps;
    private Symbol symbol;
    public LVal() {
        super(GrammarType.LVal);
        exps = new ArrayList<>();
    }
    public void checkSema(SymbolTable symbolTable) {
        symbol = symbolTable.getSymbol(ident.getValue());
        if (symbol == null) {
            ErrorLog.addError(ErrorType.UNDEFINED_IDENFR, ident.getLine());
        }
        if (hasExps()) {
            for (Exp exp : exps) {
                exp.checkSema(symbolTable);
            }
        }
    }
    @Override
    public DataType getDataType() {
        if (symbol.getType().isArrayTy()) {
            Type type = symbol.getType();
            for (int i = 0; i < exps.size(); i++) {
                type = ((ArrayType)type).getElementType();
            }
            return type.isIntegerTy() ? (IntegerType)type : new PointerType(((ArrayType)type).getElementType());
        } else if (symbol.getType().isPointerTy()) {
            Type referencedType = ((PointerType)symbol.getType()).getReferencedType();
            if (referencedType.isIntegerTy()) {
                return exps.size() == 1 ? (IntegerType)referencedType : (PointerType)symbol.getType();
            } else {
                return switch (exps.size()) {
                    case 0 -> (PointerType) symbol.getType();
                    case 1 -> new PointerType(((ArrayType) referencedType).getElementType());
                    default -> (IntegerType) ((ArrayType) referencedType).getElementType();
                };
            }
        } else { return (DataType) symbol.getType(); }
    }
    public void setIdent(Token ident) { this.ident = ident; }
    public void addExp(Exp exp) { exps.add(exp); }
    public Token getIdent() { return ident; }
    public boolean hasExps() { return !exps.isEmpty(); }
    public ArrayList<Exp> getExps() { return exps; }
    public Symbol getSymbol() { return symbol; }
    public int getOpResult() { // calculate LVal's result，require Symbol's initializer
        assert null == symbol.getConstantInit() && !symbol.isGlobal() :  "calculate uninitialized lval during sema.";
        if (isGlobal && null == symbol.getConstantInit())
            return 0;
        else if (hasExps()) {
            ConstantArray constantArray = (ConstantArray)symbol.getConstantInit();
            if (exps.size() == 1)
                return ((ConstantInt)constantArray.getElement(exps.get(0).getOpResult())).getVal();
            else {
                Constant element = constantArray.getElement(exps.get(0).getOpResult(), exps.get(1).getOpResult());
                return ((ConstantInt) element).getVal();
            }
        } else
            return ((ConstantInt)symbol.getConstantInit()).getVal();
    }
}
