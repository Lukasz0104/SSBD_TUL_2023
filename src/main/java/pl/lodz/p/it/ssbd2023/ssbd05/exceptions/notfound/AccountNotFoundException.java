package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AccountNotFoundException extends AppNotFoundException {
    public AccountNotFoundException() {
        super(I18n.ACCOUNT_NOT_FOUND);
    }
}
