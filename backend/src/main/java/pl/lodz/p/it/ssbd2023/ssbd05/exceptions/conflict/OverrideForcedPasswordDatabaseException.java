package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class OverrideForcedPasswordDatabaseException extends AppConflictException {

    public OverrideForcedPasswordDatabaseException() {
        super(I18n.OVERRIDE_FORCED_PASSWORD_DATABASE_EXCEPTION);
    }
}
