package shikiri.board.exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        if (isSynchronousMode()) {
            return this;
        } else {
            return super.fillInStackTrace();
        }
    }

    private boolean isSynchronousMode() {
        return false;
    }
}