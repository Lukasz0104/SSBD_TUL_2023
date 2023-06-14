package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class BuildingNotFoundException extends AppNotFoundException {
    public BuildingNotFoundException() {
        super(I18n.BUILDING_NOT_FOUND);
    }
}
