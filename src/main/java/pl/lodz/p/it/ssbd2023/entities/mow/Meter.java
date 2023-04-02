package pl.lodz.p.it.ssbd2023.entities.mow;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.entities.AbstractEntity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meter")
@NoArgsConstructor
public class Meter extends AbstractEntity implements Serializable {
    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @OneToMany(mappedBy = "meter", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Reading> readings = new HashSet<>();

    public Meter(Category category, Place place) {
        this.category = category;
        this.place = place;
    }
}
