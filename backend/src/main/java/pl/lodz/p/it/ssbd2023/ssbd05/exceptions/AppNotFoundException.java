package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;


public class AppNotFoundException extends AppBaseException {

    public AppNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppNotFoundException(String message) {
        super(message);
    }

    public AppNotFoundException() {
        super(I18n.NOT_FOUND);
    }
}
