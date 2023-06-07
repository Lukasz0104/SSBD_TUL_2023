package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ReportNotFoundException extends AppNotFoundException {

    public ReportNotFoundException() {
        super(I18n.REPORT_NOT_FOUND);
    }
}
