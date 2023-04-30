package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppBadRequestException extends AppBaseException {
    public AppBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppBadRequestException(String message) {
        super(message);
    }

    public AppBadRequestException() {
        super(I18n.BAD_REQUEST);
    }
}
