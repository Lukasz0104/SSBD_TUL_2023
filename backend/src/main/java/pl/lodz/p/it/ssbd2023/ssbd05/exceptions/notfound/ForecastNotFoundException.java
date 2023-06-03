package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ForecastNotFoundException extends AppNotFoundException {

    public ForecastNotFoundException() {
        super(I18n.FORECAST_NOT_FOUND);
    }
}
