package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ConstraintViolationException extends AppConflictException {

    public ConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintViolationException() {
        super(I18n.CONSTRAINT_VIOLATION);
    }

    public ConstraintViolationException(String message) {
        super(message);
    }
}
