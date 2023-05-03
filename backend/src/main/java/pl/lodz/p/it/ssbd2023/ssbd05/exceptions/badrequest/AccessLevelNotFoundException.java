package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AccessLevelNotFoundException extends AppBadRequestException {
    public AccessLevelNotFoundException() {
        super(I18n.ACCESS_LEVEL_NOT_FOUND);
    }
}
