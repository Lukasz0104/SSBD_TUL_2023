package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class CostAlreadyExistsException extends AppConflictException {
    public CostAlreadyExistsException() {
        super(I18n.COST_ALREADY_EXISTS);
    }
}
