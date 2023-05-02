package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ForcePasswordChangeDatabaseException extends AppConflictException {

    public ForcePasswordChangeDatabaseException() {
        super(I18n.FORCE_PASSWORD_CHANGE_DATABASE_EXCEPTION);
    }
}
