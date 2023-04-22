package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;

public class ConstraintViolationException extends AppConflictException {

    public ConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
