package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "owner_data")
@DiscriminatorValue("owner")
@NoArgsConstructor
public class OwnerData extends AccessLevel implements Serializable {

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

    public OwnerData(Account account, Address address) {
        super(AccessTypes.OWNER, account);
        this.address = address;
    }
}
