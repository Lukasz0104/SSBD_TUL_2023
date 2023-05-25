package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

public enum AccessType {
    OWNER(I18n.ACCESS_LEVEL_OWNER),
    ADMIN(I18n.ACCESS_LEVEL_ADMINISTRATOR),
    MANAGER(I18n.ACCESS_LEVEL_MANAGER);

    private final String localizedNameKey;

    AccessType(String key) {
        this.localizedNameKey = key;
    }
}
