package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class UnaccessibleForecastException extends AppForbiddenException {

    public UnaccessibleForecastException() {
        super(I18n.UNACCESSIBLE_FORECAST);
    }
}
