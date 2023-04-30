package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppConflictException extends AppBaseException {
    public AppConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppConflictException(String message) {
        super(message);
    }

    public AppConflictException() {
        super(I18n.CONFLICT);
    }
}
