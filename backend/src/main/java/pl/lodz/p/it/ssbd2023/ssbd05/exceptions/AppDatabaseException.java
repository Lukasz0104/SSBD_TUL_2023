package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

public class AppDatabaseException extends AppBaseException {

    public AppDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppDatabaseException(Throwable cause) {
        super(cause);
    }
}
