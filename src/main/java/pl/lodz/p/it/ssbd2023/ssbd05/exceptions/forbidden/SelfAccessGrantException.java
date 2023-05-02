package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class SelfAccessGrantException extends AppForbiddenException {
    public SelfAccessGrantException() {
        super(I18n.ACCESS_GRANTED_SELF);
    }
}
