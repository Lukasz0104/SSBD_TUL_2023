package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.entities.Address;

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
public class Building extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "building", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter
    private final Set<Place> places = new HashSet<>();

    @Embedded
    @NotNull
    @Getter
    @Setter
    private Address address;

    public Building(Address address) {
        this.address = address;
    }
}
