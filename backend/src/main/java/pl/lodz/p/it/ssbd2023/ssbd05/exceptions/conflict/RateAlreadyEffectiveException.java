package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class RateAlreadyEffectiveException extends AppConflictException {
    public RateAlreadyEffectiveException() {
        super(I18n.RATE_ALREADY_EFFECTIVE);
    }
}
