package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Year;

@ToString
@Entity
@Table(name = "report", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "place_id", "category_id"}))
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Report.findByYear",
        query = "SELECT r FROM Report r WHERE r.year = :year"),
    @NamedQuery(
        name = "Report.findByPlaceIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.id = :placeId
                  AND r.year = :year ORDER BY r.category.name ASC
            """),
    @NamedQuery(
        name = "Report.findByBuildingIdAndYear",
        query = """
            SELECT r FROM Report r
            WHERE r.place.building.id = :buildingId
                  AND r.year = :year
            """),
    @NamedQuery(
        name = "Report.findYearsByPlaceId",
        query = "SELECT DISTINCT r.year FROM Report r WHERE r.place.id = :placeId")
})
@EntityListeners({EntityControlListenerMOW.class})
public class Report extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "year", nullable = false)
    @Getter
    @Setter
    private Year year;

    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = "total_cost", nullable = false, scale = 6, precision = 38)
    @Getter
    @Setter
    private BigDecimal totalCost;

    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = "total_consumption", nullable = false, scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal totalConsumption;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    public Report(Year year, BigDecimal totalCost, BigDecimal totalConsumption, Place place, Category category) {
        this.year = year;
        this.totalCost = totalCost;
        this.totalConsumption = totalConsumption;
        this.place = place;
        this.category = category;
    }
}
