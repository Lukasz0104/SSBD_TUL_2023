package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class RateNotUniqueException extends ConstraintViolationException {
    public RateNotUniqueException() {
        super(I18n.RATE_NOT_UNIQUE);
    }
}
