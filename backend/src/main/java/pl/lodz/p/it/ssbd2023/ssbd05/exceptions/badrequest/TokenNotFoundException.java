package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class TokenNotFoundException extends AppBadRequestException {
    public TokenNotFoundException() {
        super(I18n.TOKEN_NOT_FOUND);
    }
}
