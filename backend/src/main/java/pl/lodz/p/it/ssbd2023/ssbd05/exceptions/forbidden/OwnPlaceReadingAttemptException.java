package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class OwnPlaceReadingAttemptException extends AppForbiddenException {

    public OwnPlaceReadingAttemptException() {
        super(I18n.OWN_PLACE_READING_ATTEMPT_EXCEPTION);
    }
}
