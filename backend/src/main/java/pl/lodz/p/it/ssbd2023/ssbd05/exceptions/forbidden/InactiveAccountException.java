package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class InactiveAccountException extends AppForbiddenException {
    public InactiveAccountException() {
        super(I18n.INACTIVE_ACCOUNT);
    }
}
