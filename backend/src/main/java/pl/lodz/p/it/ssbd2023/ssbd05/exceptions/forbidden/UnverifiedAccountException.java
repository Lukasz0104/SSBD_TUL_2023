package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class UnverifiedAccountException extends AppForbiddenException {
    public UnverifiedAccountException() {
        super(I18n.UNVERIFIED_ACCOUNT);
    }
}
