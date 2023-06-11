package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class NotEnoughDaysAfterInitialReadingException extends AppConflictException {
    public NotEnoughDaysAfterInitialReadingException() {
        super(I18n.NOT_ENOUGH_DAYS_AFTER_INITIAL_READING_EXCEPTION);
    }
}
