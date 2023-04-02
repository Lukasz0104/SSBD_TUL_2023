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
@Table(name = "forecast", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "month", "place_id", "rate_id"}))
@NoArgsConstructor
public class Forecast extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @Column(name = "value", nullable = false)
    @Getter
    @Setter
    private BigDecimal value;


    @Column(name = "real_value")
    @Getter
    @Setter
    private BigDecimal realValue;

    @NotNull
    @Basic(optional = false)
    @Column(name = "amount", nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "rate_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Rate rate;

    public Forecast(Year year, Month month, BigDecimal value, BigDecimal realValue, BigDecimal amount, Place place,
                    Rate rate) {
        this.year = year;
        this.month = month;
        this.value = value;
        this.realValue = realValue;
        this.amount = amount;
        this.place = place;
        this.rate = rate;
    }

    public Forecast(Year year, Month month, BigDecimal value, BigDecimal amount, Place place, Rate rate) {
        this.year = year;
        this.month = month;
        this.value = value;
        this.amount = amount;
        this.place = place;
        this.rate = rate;
    }
}
