package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class InvalidDateFormatException extends AppBadRequestException {
    public InvalidDateFormatException() {
        super(I18n.INVALID_DATE_FORMAT);
    }

}
