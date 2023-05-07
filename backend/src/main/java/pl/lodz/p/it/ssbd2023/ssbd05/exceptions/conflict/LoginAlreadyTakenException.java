package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class LoginAlreadyTakenException extends ConstraintViolationException {
    public LoginAlreadyTakenException() {
        super(I18n.LOGIN_ALREADY_TAKEN);
    }
}
