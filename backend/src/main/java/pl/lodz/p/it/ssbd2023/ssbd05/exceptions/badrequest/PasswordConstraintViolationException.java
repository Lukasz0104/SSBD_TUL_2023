package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class PasswordConstraintViolationException extends AppBadRequestException {
    public PasswordConstraintViolationException() {
        super(I18n.PASSWORD_CONSTRAINT);
    }
}
