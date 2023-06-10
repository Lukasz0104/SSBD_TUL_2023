package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class RateNotFoundException extends AppNotFoundException {
    public RateNotFoundException() {
        super(I18n.RATE_NOT_FOUND);
    }
}
