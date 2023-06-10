package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class CategoryNotInUseException extends AppConflictException {
    public CategoryNotInUseException() {
        super(I18n.CATEGORY_NOT_IN_USE);
    }
}
