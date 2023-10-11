package frontend.parser.astNode;

import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;

public class ConstDecl extends AstNode {
    private BType bType;
    private ArrayList<ConstDef> constDefs;
    public ConstDecl() {
        super(GrammarType.ConstDecl);
        constDefs = new ArrayList<>();
    }
    public void addConstDef(ConstDef constDef) { constDefs.add(constDef); }
    public void setBType(BType bType) { this.bType = bType; }
    public BType getBType() { return bType; }
    public ArrayList<ConstDef> getConstDefs() { return constDefs; }
    public void checkSema(SymbolTable symbolTable) {
        for (ConstDef constDef : constDefs) {
            constDef.setGlobal(isGlobal);
            constDef.checkSema(symbolTable);
        }
    }
}
