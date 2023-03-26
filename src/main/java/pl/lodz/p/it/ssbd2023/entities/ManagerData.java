package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "manager_data")
@DiscriminatorValue("manager")
@NoArgsConstructor
public class ManagerData extends AccessLevel implements Serializable {

    private static final long serialVersionUID = 1L;


    @Embedded
    @NotNull
    @Getter
    @Setter
    private Address address;

    @NotNull
    @Basic(optional = false)
    @Getter
    @Setter
    @Column(name = "license_number")
    private String licenseNumber;

    public ManagerData(Account account, Address address) {
        super(AccessTypes.MANAGER, account);
        this.address = address;
    }
}
