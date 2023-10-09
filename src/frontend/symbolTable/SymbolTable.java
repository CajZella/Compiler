package frontend.symbolTable;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import util.MyLinkedList;
import util.MyLinkedNode;
import java.util.HashMap;
import java.util.Iterator;

// 方便错误处理和帮助生成ir
// 每张symbol是一个分程序scope
public class SymbolTable extends MyLinkedNode {
    private final HashMap<String, Symbol> table;
    private final SymbolTable parent;
    private MyLinkedList<SymbolTable> childTable;
    public SymbolTable(SymbolTable parent) {
        this.table = new HashMap<>();
        this.childTable = new MyLinkedList<>();
        this.parent = parent;
        if (null != parent) parent.addChild(this);
    }
    public void addSymbol(Symbol symbol) {
        this.table.put(symbol.getIdent(), symbol);
    }
    public boolean checkSymbolWhenDecl(Token ident) {
        if (this.table.containsKey(ident.getValue())) {
            ErrorLog.addError(ErrorType.DUPLICATE_IDENFR, ident.getLine());
            return true;
        }
        return false;
    }
    // 使用时查表，如果没有则返回null
    public Symbol getSymbol(String ident) {
        SymbolTable p = this;
        while (p != null) {
            if (p.table.containsKey(ident)) return p.table.get(ident);
            p = p.parent;
        }
        return null;
    }

    public SymbolTable getParent() { return this.parent; }
    public SymbolTable getFirstChild() {return childTable.getHead(); }
    public void addChild(SymbolTable table) { childTable.insertAtTail(table);}
}
