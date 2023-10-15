package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;
import ir.Value;
import ir.types.DataType;

import java.util.ArrayList;

public abstract class AstNode {
    protected GrammarType grammarType;
    protected ArrayList<AstNode> elements;
    protected FuncType funcType = null;
    protected boolean isGlobal = false;
    protected boolean isInLoop = false;
    public AstNode(GrammarType grammarType) {
        this.grammarType = grammarType;
        elements = new ArrayList<>();
    }
    public int size() { return elements.size(); }
    public DataType getDataType() {return elements.get(0).getDataType();}
    public void setFuncType(FuncType funcType) {
        this.funcType = funcType;
    }
    public void setGlobal(boolean isGlobal) { this.isGlobal = isGlobal; }
    public FuncType getFuncType() {
        return funcType;
    }
    public void setInLoop(boolean isInLoop) { this.isInLoop = isInLoop; }
    public void addElement(AstNode element) {
        elements.add(element);
    }
    public AstNode get(int index) {
        return elements.get(index);
    }
    public ArrayList<AstNode> getElements() { return elements; }
    public AstNode getLast() { return elements.get(elements.size() - 1); }

    public boolean isStmt() { return grammarType == GrammarType.Stmt; }
    public boolean isMulExp() { return grammarType == GrammarType.MulExp; }
    public boolean isRelExp() { return grammarType == GrammarType.RelExp; }
    public boolean isEqExp() { return grammarType == GrammarType.EqExp; }
    public boolean isLAndExp() { return grammarType == GrammarType.LAndExp; }
    public boolean isLVal() { return grammarType == GrammarType.LVal; }
    public boolean isUnaryExp() { return grammarType == GrammarType.UnaryExp; }
    public boolean isExp() { return grammarType == GrammarType.Exp; }
    public boolean isNumber() { return grammarType == GrammarType.Number; }
    public String toString() {
        return String.format("<%s>", grammarType.toString());
    }
    public abstract void checkSema(SymbolTable symbolTable);
}
