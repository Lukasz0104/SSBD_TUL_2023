package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class LicenseNumberAlreadyTakenException extends ConstraintViolationException {
    public LicenseNumberAlreadyTakenException() {
        super(I18n.LICENSE_NUMBER_ALREADY_TAKEN);
    }
}
