package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

public class DatabaseException extends AppBaseException {

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
