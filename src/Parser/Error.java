package Parser;

public class Error {
    public static void handleError(String msg) {
        // todo
        System.err.println("Error: "+ msg);
        System.exit(-1);
    }
}
