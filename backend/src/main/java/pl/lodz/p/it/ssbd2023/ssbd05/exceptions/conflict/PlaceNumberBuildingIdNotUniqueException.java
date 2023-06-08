package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class PlaceNumberBuildingIdNotUniqueException extends AppConflictException {
    public PlaceNumberBuildingIdNotUniqueException() {
        super(I18n.PLACE_NUMBER_BUILDING_ID_NOT_UNIQUE);
    }
}
