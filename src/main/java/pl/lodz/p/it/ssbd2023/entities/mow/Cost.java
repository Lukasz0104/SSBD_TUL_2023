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
import java.time.Month;
import java.time.Year;

@Entity
@Table(name = "cost", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "month", "category_id"}))
@NoArgsConstructor
public class Cost extends AbstractEntity implements Serializable {

    @NotNull
    @Basic(optional = false)
    @Column(name = "year", nullable = false)
    @Getter
    @Setter
    private Year year;

    @NotNull
    @Basic(optional = false)
    @Column(name = "month", nullable = false)
    @Getter
    @Setter
    private Month month;

    @NotNull
    @Basic(optional = false)
    @Column(name = "total_cost", nullable = false)
    @Getter
    @Setter
    private BigDecimal totalCost;


    @Column(name = "total_consumption")
    @Getter
    @Setter
    private BigDecimal totalConsumption;

    @NotNull
    @Basic(optional = false)
    @Column(name = "real_rate", nullable = false)
    @Getter
    @Setter
    private BigDecimal realRate;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    public Cost(Year year, Month month, BigDecimal totalCost, BigDecimal totalConsumption, BigDecimal realRate,
                Category category) {
        this.year = year;
        this.month = month;
        this.totalCost = totalCost;
        this.totalConsumption = totalConsumption;
        this.realRate = realRate;
        this.category = category;
    }
}
