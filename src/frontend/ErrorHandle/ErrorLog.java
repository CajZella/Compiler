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
    public static boolean hasError() {
        return errorList.size() != 0;
    }
    public static int size() { return errorList.size(); }
    public static void remain(int num) {
        errorList = new ArrayList<>(errorList.subList(0, num));
    }
    public static void sort() {
        errorList.sort((o1, o2) -> {
            if (o1.getLine() < o2.getLine()) {
                return -1;
            } else if (o1.getLine() > o2.getLine()) {
                return 1;
            } else {
                return 0;
            }
        });
    }
    public static String print() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Error error : errorList) {
            stringBuilder.append(error.toString() + "\n");
        }
        return stringBuilder.toString();
    }
}
