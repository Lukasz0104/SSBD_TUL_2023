package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class NotEnoughDaysBetweenReliableReadingsException extends AppConflictException {
    public NotEnoughDaysBetweenReliableReadingsException() {
        super(I18n.NOT_ENOUGH_DAYS_BETWEEN_RELIABLE_READINGS_EXCEPTION);
    }
}
