package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class PlaceNotFoundException extends AppNotFoundException {
    public PlaceNotFoundException() {
        super(I18n.PLACE_NOT_FOUND);
    }

}
