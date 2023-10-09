package frontend.parser.astNode;

import frontend.ErrorHandle.ErrorLog;
import frontend.ErrorHandle.ErrorType;
import frontend.lexer.Token;
import frontend.symbolTable.SymbolTable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StmtPrintf extends Stmt {
    private int printfLine;
    private Token formatString;
    private ArrayList<Exp> exps;
    public StmtPrintf() {
        super(StmtType.StmtPrintf);
        exps = new ArrayList<>();
    }
    public void setFormatString(Token formatString) {
        this.formatString = formatString;
    }
    public void addExp(Exp exp) {
        exps.add(exp);
    }
    public void setPrintfLine(int printfLine) { this.printfLine = printfLine; }
    public Token getFormatString() {
        return formatString;
    }
    public boolean hasExps() {
        return !exps.isEmpty();
    }
    public ArrayList<Exp> getExps() {
        return exps;
    }
    public void checkSema(SymbolTable symbolTable) {
        String strCon = formatString.getValue();
        String regrex = "%d";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(strCon);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        if (count != exps.size()) {
            ErrorLog.addError(ErrorType.PRINTF_MISMATCHED, printfLine);
        }
        for (Exp exp : exps) {
            exp.checkSema(symbolTable);
        }
    }
}
