package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "building")
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Building.findAll",
        query = "SELECT b FROM Building  b"),
    @NamedQuery(
        name = "Building.findById",
        query = "SELECT b FROM Building  b WHERE b.id = :id"),
    @NamedQuery(
        name = "Building.findByAddress",
        query = "SELECT b FROM Building  b WHERE b.address = :address"),
    @NamedQuery(
        name = "Building.findByBuildingNumber",
        query = "SELECT b FROM Building  b WHERE b.address.buildingNumber = :buildingNumber")
})
@EntityListeners({EntityControlListenerMOW.class})
public class Building extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @OneToMany(mappedBy = "building", cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Getter
    private Set<Place> places = new HashSet<>();

    @Embedded
    @NotNull
    @Getter
    @Setter
    private Address address;

    public Building(Address address) {
        this.address = address;
    }
}
