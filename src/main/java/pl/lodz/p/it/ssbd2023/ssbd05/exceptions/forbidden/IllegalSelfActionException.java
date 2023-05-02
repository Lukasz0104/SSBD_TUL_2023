package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class IllegalSelfActionException extends AppForbiddenException {

    public IllegalSelfActionException() {
        super(I18n.ILLEGAL_SELF_ACTION);
    }
}
