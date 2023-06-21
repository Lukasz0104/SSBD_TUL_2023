package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidLicenseNumber;

import java.io.Serializable;

@Entity
@Table(name = "manager_data")
@DiscriminatorValue(MANAGER)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ManagerData extends AccessLevel implements Serializable {

    private static final long serialVersionUID = 1L;


    @Embedded
    @NotNull
    @Getter
    @Setter
    private Address address;

    @ValidLicenseNumber
    @NotNull
    @Basic(optional = false)
    @Getter
    @Setter
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    public ManagerData(Account account, Address address, String licenseNumber) {
        super(AccessType.MANAGER, account);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }

    public ManagerData(Long id, Long version, Address address, String licenseNumber) {
        super(id, version, AccessType.MANAGER);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }

    public ManagerData(Long id, Long version, Address address, boolean active, String licenseNumber) {
        super(id, version, AccessType.MANAGER, active);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }

    public ManagerData(Address address, String licenseNumber) {
        super(AccessType.MANAGER);
        this.address = address;
        this.licenseNumber = licenseNumber;
    }
}
