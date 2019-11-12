package gt.app.service.file;

public class RetrievalException extends RuntimeException {

    public RetrievalException(String message) {
        super(message);
    }

    public RetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
