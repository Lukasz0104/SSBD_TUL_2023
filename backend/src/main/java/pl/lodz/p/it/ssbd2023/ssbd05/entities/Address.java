package pl.lodz.p.it.ssbd2023.ssbd05.entities;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.PostalCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Access(AccessType.FIELD)
public class Address implements Serializable {

    @PostalCode
    @NotNull
    @Basic(optional = false)
    @Size(min = 6, max = 6)
    @Column(name = "postal_code", nullable = false, length = 6)
    private String postalCode;

    @NotNull
    @Basic(optional = false)
    @Size(min = 2, max = 85)
    @Pattern(regexp = "[A-ZĄĆĘŁÓŚŹŻ]+.*")
    @Column(name = "city", nullable = false, length = 85)
    private String city;

    @NotNull
    @Basic(optional = false)
    @Size(max = 85)
    @Column(name = "street", nullable = false, length = 85)
    private String street;

    @Positive
    @NotNull
    @Basic(optional = false)
    @Column(name = "building_number", nullable = false)
    private Integer buildingNumber;
}
