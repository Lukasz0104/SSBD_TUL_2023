package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppConflictException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class CategoryInUseException extends AppConflictException {

    public CategoryInUseException() {
        super(I18n.CATEGORY_IN_USE);
    }
}
