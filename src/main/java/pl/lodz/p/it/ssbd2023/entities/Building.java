package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "building")
@NoArgsConstructor
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
