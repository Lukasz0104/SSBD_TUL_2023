package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBadRequestException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class CategoryNotFoundException extends AppBadRequestException {
    public CategoryNotFoundException() {
        super(I18n.CATEGORY_NOT_FOUND);
    }
}
