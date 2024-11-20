package by.bsu.dependency.exception;

public class ApplicationContextNotStartedException extends RuntimeException {
    public ApplicationContextNotStartedException(String message) {
        super(message);
    }
}
