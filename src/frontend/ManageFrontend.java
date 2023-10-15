package frontend;

import frontend.ErrorHandle.ErrorLog;
import frontend.parser.Parser;
import frontend.parser.astNode.CompUnit;
import frontend.symbolTable.SymbolTable;
import ir.Module;

public class ManageFrontend {
    private final static Parser parser = new Parser();
    private static CompUnit compUnit;
    private final static SymbolTable rootTab = new SymbolTable(null);
    private static Visitor visitor;

    public static void run() {
        try {
            compUnit = parser.parseCompUnit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        compUnit.checkSema(rootTab);
        visitor = new Visitor(rootTab);
        if (!ErrorLog.hasError()) {
            visitor.visitCompUnit(compUnit);
        }
    }
    public static Module getModule() { return visitor.getModule(); }
}
