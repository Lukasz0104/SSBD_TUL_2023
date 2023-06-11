package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ForecastAlreadyExistsException extends AppConflictException {

    public ForecastAlreadyExistsException() {
        super(I18n.FORECAST_ALREADY_EXISTS);
    }
}
