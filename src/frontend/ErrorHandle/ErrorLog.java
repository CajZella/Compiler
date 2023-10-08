package frontend.ErrorHandle;

import frontend.parser.astNode.StmtPrintf;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ErrorLog {
    private static ArrayList<Error> errorList = new ArrayList<>();
    public static void addError(Error error) {
        errorList.add(error);
    }
    public static void addError(ErrorType errorType, int line) {
        errorList.add(new Error(errorType, line));
    }
    public static String printErrorLog() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Error error : errorList) {
            stringBuilder.append(error.toString() + "\n");
        }
        return stringBuilder.toString();
    }
    public static void checkPrintf(StmtPrintf stmtPrintf, int line) {
        String strCon = stmtPrintf.getFormatString().getValue();
        String regrex = "%d";
        Pattern pattern = Pattern.compile(regrex);
        Matcher matcher = pattern.matcher(strCon);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        if (count != stmtPrintf.getExps().size()) {
            ErrorLog.addError(ErrorType.PRINTF_MISMATCHED, line);
        }
    }
}
