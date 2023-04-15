package pl.lodz.p.it.ssbd2023.ssbd05.entities;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Access(AccessType.FIELD)
public class Address implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Size(min = 6, max = 6)
    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @NotNull
    @Basic(optional = false)
    @Size(min = 2, max = 85)
    @Column(name = "city", nullable = false)
    private String city;

    @NotNull
    @Basic(optional = false)
    @Size(min = 85)
    @Column(name = "street", nullable = false)
    private String street;

    @NotNull
    @Basic(optional = false)
    @Column(name = "building_number", nullable = false)
    private Integer buildingNumber;
}
