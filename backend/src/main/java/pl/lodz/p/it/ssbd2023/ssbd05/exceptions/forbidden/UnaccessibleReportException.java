package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class UnaccessibleReportException extends AppForbiddenException {

    public UnaccessibleReportException() {
        super(I18n.UNACCESSIBLE_REPORT);
    }
}
