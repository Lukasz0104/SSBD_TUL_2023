package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppRollbackLimitExceededException extends AppBaseException {

    public AppRollbackLimitExceededException() {
        super(I18n.ROLLBACK_LIMIT_EXCEEDED);
    }
}
