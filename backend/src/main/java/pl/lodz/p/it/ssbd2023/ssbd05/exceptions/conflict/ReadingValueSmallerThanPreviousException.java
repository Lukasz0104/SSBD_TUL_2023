package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ReadingValueSmallerThanPreviousException extends AppConflictException {
    public ReadingValueSmallerThanPreviousException() {
        super(I18n.READING_VALUE_SMALLER_THAN_PREVIOUS_EXCEPTION);
    }
}
