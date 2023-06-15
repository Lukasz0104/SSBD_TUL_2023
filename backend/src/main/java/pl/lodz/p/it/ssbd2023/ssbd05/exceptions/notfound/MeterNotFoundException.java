package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class MeterNotFoundException extends AppNotFoundException {
    public MeterNotFoundException() {
        super(I18n.METER_NOT_FOUND);
    }

}
