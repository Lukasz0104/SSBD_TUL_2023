package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.entities.AbstractEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Year;

@Entity
@Table(name = "report", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "place_id", "category_id"}))
@NoArgsConstructor
public class Report extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "year", nullable = false)
    @Getter
    @Setter
    private Year year;

    @NotNull
    @Basic(optional = false)
    @Column(name = "total_cost", nullable = false)
    @Getter
    @Setter
    private BigDecimal totalCost;


    @NotNull
    @Basic(optional = false)
    @Column(name = "total_consumption", nullable = false)
    @Getter
    @Setter
    private BigDecimal totalConsumption;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
