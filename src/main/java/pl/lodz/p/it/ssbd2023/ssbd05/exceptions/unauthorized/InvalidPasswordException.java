package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.unauthorized;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppUnauthorizedException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class InvalidPasswordException extends AppUnauthorizedException {
    public InvalidPasswordException() {
        super(I18n.INVALID_PASSWORD);
    }
}
