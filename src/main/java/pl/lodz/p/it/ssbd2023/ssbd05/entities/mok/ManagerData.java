package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;

import java.io.Serializable;

@Entity
@Table(name = "manager_data")
@DiscriminatorValue("manager")
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "ManagerData.findManagerDataByAddressPostalCode",
        query = "SELECT data FROM ManagerData data WHERE data.address.postalCode = :postalcode"),
    @NamedQuery(
        name = "ManagerData.findManagerDataByAddressCity",
        query = "SELECT data FROM ManagerData data WHERE data.address.city = :city"),
    @NamedQuery(
        name = "ManagerData.findManagerDataByAddressStreet",
        query = "SELECT data FROM ManagerData data WHERE data.address.street = :street"),
    @NamedQuery(
        name = "ManagerData.findManagerDataByAddressBuildingNumber",
        query = "SELECT data FROM ManagerData data WHERE data.address.buildingNumber = :buildingnumber"),
    @NamedQuery(
        name = "ManagerData.findManagerDataByAddressStreetAndBuildingNumber",
        query = """
            SELECT data FROM ManagerData data WHERE data.address.street = :street AND
             data.address.buildingNumber = :buildingnumber"""),
    @NamedQuery(
        name = "ManagerData.findManagerDataByLicenseNumber",
        query = "SELECT data FROM ManagerData data WHERE data.licenseNumber = :licenseNumber"),
    @NamedQuery(
        name = "ManagerData.findManagerDataByFullAddress",
        query = """
            SELECT data FROM ManagerData data WHERE data.address.city = :city AND
            data.address.street = :street AND
            data.address.buildingNumber = :buildingnumber AND
            data.address.postalCode = :postalcode
            """),
})
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
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;

    public ManagerData(Account account, Address address) {
        super(AccessType.MANAGER, account);
        this.address = address;
    }
}
