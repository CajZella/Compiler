package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

public class VarDecl extends AstNode {
    private BType bType;
    private ArrayList<VarDef> varDefs;
    public VarDecl() {
        super(GrammarType.VarDecl);
        varDefs = new ArrayList<>();
    }
    public void addVarDef(VarDef varDef) {
        varDefs.add(varDef);
    }
    public void setBType(BType bType) {
        this.bType = bType;
    }
    public BType getBType() {
        return bType;
    }
    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }
    public void addToSymbolTable(SymbolTable symbolTable) {
        for (VarDef varDef : varDefs) {
            varDef.addSymbolTable(symbolTable, bType);
        }
    }
}
