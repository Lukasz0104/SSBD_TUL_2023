package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ReadingDateBeforeInitialReadingException extends AppConflictException {
    public ReadingDateBeforeInitialReadingException() {
        super(I18n.READING_DATE_BEFORE_INITIAL_READING_EXCEPTION);
    }
}
