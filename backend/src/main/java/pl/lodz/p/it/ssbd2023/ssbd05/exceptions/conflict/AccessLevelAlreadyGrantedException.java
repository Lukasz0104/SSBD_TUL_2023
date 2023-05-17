package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AccessLevelAlreadyGrantedException extends AppConflictException {
    public AccessLevelAlreadyGrantedException() {
        super(I18n.ACCESS_LEVEL_ALREADY_GRANTED);
    }
}
