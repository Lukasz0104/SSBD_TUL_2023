package pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public class CostNotFoundException extends AppNotFoundException {
    public CostNotFoundException() {
        super(I18n.COST_NOT_FOUND);
    }
}
