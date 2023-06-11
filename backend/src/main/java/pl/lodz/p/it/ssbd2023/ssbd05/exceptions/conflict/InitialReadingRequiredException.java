package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class InitialReadingRequiredException extends AppConflictException {
    public InitialReadingRequiredException() {
        super(I18n.INITIAL_READING_REQUIRED);
    }
}
