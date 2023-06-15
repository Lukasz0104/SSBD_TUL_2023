package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ReadingValueHigherThanFutureException extends AppConflictException {
    public ReadingValueHigherThanFutureException() {
        super(I18n.READING_VALUE_HIGHER_THAN_FUTURE_EXCEPTION);
    }
}
