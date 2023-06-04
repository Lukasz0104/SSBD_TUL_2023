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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

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
        name = "Forecast.findByPlaceIdAndCategoryId",
        query = "SELECT f FROM Forecast f WHERE f.place.id = :place AND f.rate.category.id = :categoryId"),

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
        name = "Forecast.findByCategoryId",
        query = "SELECT f FROM Forecast f WHERE f.rate.category.id = :categoryId"),
    @NamedQuery(
        name = "Forecast.findByYearAndCategoryId",
        query = "SELECT f FROM Forecast f WHERE f.year = :year AND f.rate.category.id = :categoryId"),
    @NamedQuery(
        name = "Forecast.findByMonthAndCategoryId",
        query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.rate.category.id = :categoryId"),
    @NamedQuery(
        name = "Forecast.findByMonthAndYearAndCategoryId",
        query = """
            SELECT f FROM Forecast f
            WHERE f.month = :month AND f.year = :year AND f.rate.category.id = :categoryId"""),

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
    @NamedQuery(
        name = "Forecast.findByBuildingIdAndYear",
        query = "SELECT f FROM Forecast f WHERE f.year = :year AND f.place.building.id = :buildingId"),
    @NamedQuery(
        name = "Forecast.findDistinctYearsById",
        query = "SELECT DISTINCT f.year FROM Forecast f WHERE f.place.building.id = :id ORDER BY f.year"),
    @NamedQuery(
        name = "Forecast.findYearsAndMonthsByBuildingId",
        query = """
            SELECT f.year AS year, COUNT(DISTINCT f.month) AS months
                FROM Forecast f
                WHERE f.place.building.id = :id
                GROUP BY f.year
                ORDER BY f.year
            """),
})
@EntityListeners({EntityControlListenerMOW.class})
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
    @Column(name = "value", nullable = false, scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal value;

    @PositiveOrZero
    @Column(name = "real_value", scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal realValue;

    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = "amount", nullable = false, scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal amount;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "place_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Place place;

    @NotNull
    @ManyToOne(optional = false)
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
