package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public abstract class AppBaseException extends Exception {
    public AppBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
