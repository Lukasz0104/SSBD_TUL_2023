package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class AppOptimisticLockException extends AppConflictException {
    public AppOptimisticLockException() {
        super(I18n.OPTIMISTIC_LOCK);
    }
}
