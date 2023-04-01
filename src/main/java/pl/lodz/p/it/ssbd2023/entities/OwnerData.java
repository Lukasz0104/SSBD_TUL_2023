package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "owner_data")
@DiscriminatorValue("owner")
@NoArgsConstructor
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

    @NotNull
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "owners")
    @Getter
    @Setter
    private Set<Place> places = new HashSet<>();

    public OwnerData(Account account, Address address) {
        super(AccessTypes.OWNER, account);
        this.address = address;
    }
}
