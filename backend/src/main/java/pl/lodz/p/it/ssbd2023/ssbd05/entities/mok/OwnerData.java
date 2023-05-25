package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;

import java.io.Serializable;

@Entity
@Table(name = "owner_data")
@DiscriminatorValue(OWNER)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NamedQueries({
    @NamedQuery(
        name = "OwnerData.findOwnerDataByAddressPostalCode",
        query = "SELECT data FROM OwnerData data WHERE data.address.postalCode = :postalcode"),
    @NamedQuery(
        name = "OwnerData.findOwnerDataByAddressCity",
        query = "SELECT data FROM OwnerData data WHERE data.address.city = :city"),
    @NamedQuery(
        name = "OwnerData.findOwnerDataByAddressStreet",
        query = "SELECT data FROM OwnerData data WHERE data.address.street = :street"),
    @NamedQuery(
        name = "OwnerData.findOwnerDataByAddressBuildingNumber",
        query = "SELECT data FROM OwnerData data WHERE data.address.buildingNumber = :buildingnumber"),
    @NamedQuery(
        name = "OwnerData.findOwnerDataByAddressStreetAndBuildingNumber",
        query = """
            SELECT data FROM OwnerData data WHERE data.address.street = :street AND
             data.address.buildingNumber = :buildingnumber"""),
    @NamedQuery(
        name = "OwnerData.findOwnerDataByFullAddress",
        query = """
            SELECT data FROM OwnerData data WHERE data.address.city = :city AND
            data.address.street = :street AND
            data.address.buildingNumber = :buildingnumber AND
            data.address.postalCode = :postalcode
            """),
})
public class OwnerData extends AccessLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Embedded
    @NotNull
    @Getter
    @Setter
    private Address address;

    public OwnerData(Account account, Address address) {
        super(AccessType.OWNER, account);
        this.address = address;
    }

    public OwnerData(Long id, Long version, Address address) {
        super(id, version, AccessType.OWNER);
        this.address = address;
    }

    public OwnerData(Long id, Long version, Address address, boolean active) {
        super(id, version, AccessType.OWNER, active);
        this.address = address;
    }

    public OwnerData(Address address) {
        super(AccessType.OWNER);
        this.address = address;
    }
}
