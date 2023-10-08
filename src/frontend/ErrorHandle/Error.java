package frontend.ErrorHandle;

public class Error {
    private final ErrorType errorType;
    private final int line;

    public Error(ErrorType errorType, int line) {
        this.errorType = errorType;
        this.line = line;
    }
    public ErrorType getErrorType() {
        return errorType;
    }
    public int getLine() {
        return line;
    }
    @Override
    public String toString() {
        return line + " " + errorType.getType();
    }
}
