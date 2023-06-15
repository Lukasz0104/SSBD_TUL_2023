package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class BadAccessLevelException extends AppForbiddenException {
    public BadAccessLevelException() {
        super(I18n.BAD_ACCESS_LEVEL);
    }
}
