package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class PlaceNumberAlreadyTaken extends AppConflictException {
    public PlaceNumberAlreadyTaken() {
        super(I18n.PLACE_NUMBER_ALREADY_TAKEN);
    }
}
