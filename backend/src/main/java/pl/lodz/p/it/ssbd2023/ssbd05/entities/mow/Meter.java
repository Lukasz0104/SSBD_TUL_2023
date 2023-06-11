package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "meter", uniqueConstraints = @UniqueConstraint(columnNames = {"place_id", "category_id"}))
@NamedQueries({
    @NamedQuery(
        name = "Meter.findAll",
        query = "SELECT m FROM Meter m"),
    @NamedQuery(
        name = "Meter.findByIdAndOwnerLogin",
        query = """
            SELECT m FROM Meter m WHERE m.id = :id
            AND :login IN (SELECT o.account.login FROM m.place.owners o)
            """),
    @NamedQuery(
        name = "Meter.findByCategoryId",
        query = "SELECT m FROM Meter m WHERE m.category.id = :categoryId"),
    @NamedQuery(
        name = "Meter.findByCategoryName",
        query = "SELECT m FROM Meter m WHERE m.category.name = :categoryName"),
    @NamedQuery(
        name = "Meter.findByPlaceId",
        query = "SELECT m FROM Meter m WHERE m.place.id = :placeId"),
    @NamedQuery(
        name = "Meter.findByPlaceNumberAndBuildingId",
        query = """
            SELECT m FROM Meter m
            WHERE m.place.placeNumber = :placeNumber
                  AND m.place.building.id = :buildingId"""),
    @NamedQuery(
        name = "Meter.findByCategoryIdAndPlaceId",
        query = """
            SELECT m FROM Meter m
            WHERE m.category.id = :categoryId
                  AND m.place.id = :placeId"""),
    @NamedQuery(
        name = "Meter.findByCategoryIdAndPlaceNumberAndBuildingId",
        query = """
            SELECT m FROM Meter m
            WHERE m.category.id = :categoryId
                  AND m.place.placeNumber = :placeNumber
                  AND m.place.building.id = :buildingId"""),
    @NamedQuery(
        name = "Meter.findByCategoryNameAndPlaceId",
        query = """
            SELECT m FROM Meter m
            WHERE m.category.name = :categoryName
                  AND m.place.id = :placeId"""),
    @NamedQuery(
        name = "Meter.findByCategoryNameAndPlaceNumberAndBuildingId",
        query = """
            SELECT m FROM Meter m
            WHERE m.category.name = :categoryName
                  AND m.place.placeNumber = :placeNumber
                  AND m.place.building.id = :buildingId"""),
})
@NoArgsConstructor
@EntityListeners({EntityControlListenerMOW.class})
public class Meter extends AbstractEntity implements Serializable {
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @OneToMany(mappedBy = "meter", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Set<Reading> readings = new HashSet<>();

    @NotNull
    @Basic(optional = false)
    @Column(name = "active", nullable = false)
    @Getter
    @Setter
    private boolean active = true;

    public Meter(Category category, Place place) {
        this.category = category;
        this.place = place;
    }

    public List<Reading> getFutureReliableReadings() {
        return getReadings().stream()
            .filter(Reading::isReliable)
            .filter(r -> r.getDate().isAfter(LocalDateTime.now()))
            .sorted(Comparator.comparing(Reading::getDate).reversed())
            .toList();
    }

    public List<Reading> getPastReliableReadings() {
        return getReadings().stream()
            .filter(Reading::isReliable)
            .filter(r -> r.getDate().isBefore(LocalDateTime.now()) || r.getDate().isEqual(LocalDateTime.now()))
            .sorted(Comparator.comparing(Reading::getDate).reversed())
            .toList();
    }
}
