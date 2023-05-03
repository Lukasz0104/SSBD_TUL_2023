package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppInternalServerErrorException extends AppBaseException {
    public AppInternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppInternalServerErrorException(Throwable cause) {
        super(cause);
    }

    public AppInternalServerErrorException() {
        super(I18n.INTERNAL);
    }
}
