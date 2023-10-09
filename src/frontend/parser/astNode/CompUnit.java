package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

public class CompUnit extends AstNode {
    private ArrayList<Decl> decls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;
    public CompUnit() {
        super(GrammarType.CompUnit);
        decls = new ArrayList<>();
        funcDefs = new ArrayList<>();
    }
    public void addDecl(Decl decl) { decls.add(decl); }
    public void addFuncDef(FuncDef funcDef) { funcDefs.add(funcDef); }
    public void setMainFuncDef(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }
    public boolean hasDecls () { return !decls.isEmpty(); }
    public boolean hasFuncDefs () { return !funcDefs.isEmpty(); }
    public ArrayList<Decl> getDecls() { return decls; }
    public ArrayList<FuncDef> getFuncDefs() { return funcDefs; }
    public MainFuncDef getMainFuncDef() { return mainFuncDef; }
    public void checkSema(SymbolTable symbolTable) {
        for (Decl decl : decls) {
            decl.checkSema(symbolTable);
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.checkSema(symbolTable);
        }
        mainFuncDef.checkSema(symbolTable);
    }
}
