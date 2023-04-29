package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppForbiddenException extends AppBaseException {

    public AppForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppForbiddenException(String message) {
        super(message);
    }

    public AppForbiddenException() {
        super(I18n.FORBIDDEN);
    }
}
