package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class InaccessibleReportException extends AppForbiddenException {

    public InaccessibleReportException() {
        super(I18n.INACCESSIBLE_REPORT);
    }
}
