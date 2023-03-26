package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.*;
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

    @NotNull
    @Basic(optional = false)
    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride( name = "postalCode", column = @Column(name="postalCode", nullable = false)),
                    @AttributeOverride( name = "city", column = @Column(name = "city", nullable = false)),
                    @AttributeOverride( name = "street", column = @Column(name = "street", nullable = false)),
                    @AttributeOverride( name = "buildingNumber", column = @Column(name = "buildingNumber", nullable = false))
            }
    )
    @Getter
    @Setter
    private Address address;

    public ManagerData(Account account, Address address) {
        super(AccessTypes.MANAGER, account);
        this.address = address;
    }
}
