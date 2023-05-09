package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class EmailAddressAlreadyTakenException extends ConstraintViolationException {
    public EmailAddressAlreadyTakenException() {
        super(I18n.EMAIL_ADDRESS_ALREADY_TAKEN);
    }
}
