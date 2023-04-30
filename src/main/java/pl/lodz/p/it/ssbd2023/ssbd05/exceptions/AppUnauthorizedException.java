package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppUnauthorizedException extends AppBaseException {
    public AppUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppUnauthorizedException(String message) {
        super(message);
    }

    public AppUnauthorizedException() {
        super(I18n.UNAUTHORIZED);
    }
}
