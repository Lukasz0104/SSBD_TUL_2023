package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Entity
@Table(name = "admin_data")
@DiscriminatorValue(ADMIN)
@EqualsAndHashCode(callSuper = true)
public class AdminData extends AccessLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    public AdminData(Account account) {
        super(AccessType.ADMIN, account);
    }

    public AdminData() {
        super(AccessType.ADMIN);
    }

    public AdminData(Long id, Long version) {
        super(id, version, AccessType.ADMIN);
    }

    public AdminData(Long id, Long version, boolean active) {
        super(id, version, AccessType.ADMIN, active);
    }


}
