package pl.lodz.p.it.ssbd2023.entities;

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
@Table(name = "owner_data")
@DiscriminatorValue("owner")
@NoArgsConstructor
public class OwnerData extends AccessLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Embedded
    @NotNull
    @Getter
    @Setter
    private Address address;

    public OwnerData(Account account, Address address) {
        super(AccessTypes.OWNER, account);
        this.address = address;
    }
}
