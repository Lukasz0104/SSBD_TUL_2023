package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class ExpiredTokenException extends AppBadRequestException {
    public ExpiredTokenException() {
        super(I18n.EXPIRED_TOKEN);
    }
}
