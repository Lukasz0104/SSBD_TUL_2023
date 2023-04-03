package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.*;
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
@NamedQueries({
        @NamedQuery(
                name = "Forecast.findAll",
                query = "SELECT f FROM Forecast f"),
        @NamedQuery(
                name = "Forecast.findById",
                query = "SELECT f FROM Forecast f WHERE f.id = :id"),
        @NamedQuery(
                name = "Forecast.findByYear",
                query = "SELECT f FROM Forecast f WHERE f.year = :year"),
        @NamedQuery(
                name = "Forecast.findByMonth",
                query = "SELECT f FROM Forecast f WHERE f.month = :month"),
        @NamedQuery(
                name = "Forecast.findByMonthAndYear",
                query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.year = :year"),
        @NamedQuery(
                name = "Forecast.findByPlaceIdAndCategory",
                query = "SELECT f FROM Forecast f WHERE f.place.id = :place AND f.rate.category = :category"),

        // place queries
        @NamedQuery(
                name = "Forecast.findByPlaceId",
                query = "SELECT f FROM Forecast f WHERE f.place.id = :place"),
        @NamedQuery(
                name = "Forecast.findByPlaceIdAndMonth",
                query = "SELECT f FROM Forecast f WHERE f.place.id = :place AND f.month = :month"),
        @NamedQuery(
                name = "Forecast.findByPlaceIdAndYear",
                query = "SELECT f FROM Forecast f WHERE f.place.id = :place AND f.year = :year"),
        @NamedQuery(
                name = "Forecast.findByPlaceIdAndYearAndMonth",
                query = "SELECT f FROM Forecast f WHERE f.place.id = :place AND f.month = :month AND f.year = :year"),

        // category queries
        @NamedQuery(
                name = "Forecast.findByCategory",
                query = "SELECT f FROM Forecast f WHERE f.rate.category = :category"),
        @NamedQuery(
                name = "Forecast.findByYearAndCategory",
                query = "SELECT f FROM Forecast f WHERE f.year = :year AND f.rate.category = :category"),
        @NamedQuery(
                name = "Forecast.findByMonthAndCategory",
                query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.rate.category = :category"),
        @NamedQuery(
                name = "Forecast.findByMonthAndYearAndCategory",
                query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.year = :year AND f.rate.category = :category"),

        // rate queries
        @NamedQuery(
                name = "Forecast.findByRateId",
                query = "SELECT f FROM Forecast f WHERE f.rate.id = :rate"),
        @NamedQuery(
                name = "Forecast.findByYearAndRateId",
                query = "SELECT f FROM Forecast f WHERE f.year = :year AND f.rate.id = :rate"),
        @NamedQuery(
                name = "Forecast.findByMonthAndRateId",
                query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.rate.id = :rate"),
        @NamedQuery(
                name = "Forecast.findByMonthAndYearAndRateId",
                query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.year = :year AND f.rate.id = :rate"),

})
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
