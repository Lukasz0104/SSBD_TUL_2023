package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "city_dict")
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "CityDict.findCitiesStartingWithLimit10",
        query = "SELECT a.city FROM CityDict a WHERE a.city LIKE CONCAT(:pattern,'%')")})
public class CityDict implements Serializable {

    @Id
    @Getter
    @Basic(optional = false)
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Getter
    @Basic(optional = false)
    @Size(min = 2, max = 85)
    @Pattern(regexp = "[A-ZĄĆĘŁÓŚŹŻ]+.*")
    @Column(name = "city", length = 85, unique = true, updatable = false)
    private String city;

    @Setter(lombok.AccessLevel.NONE)
    @Getter(lombok.AccessLevel.NONE)
    private static final long serialVersionUID = 1L;

    public CityDict(String city) {
        this.city = city;
    }
}