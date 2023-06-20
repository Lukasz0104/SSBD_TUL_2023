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
        name = "Forecast.findByMonthAndYear",
        query = "SELECT f FROM Forecast f WHERE f.month = :month AND f.year = :year"),
    @NamedQuery(
        name = "Forecast.findByPlaceIdAndCategoryIdAndYearAndAfterMonth",
        query = """
            SELECT f FROM Forecast f
            WHERE f.place.id = :place
            AND f.rate.category.id = :categoryId
            AND f.year = :year
            AND f.month > :month
            """),
    @NamedQuery(
        name = "Forecast.findByPlaceIdAndCategoryIdAndYearAndAfterOrEqualMonth",
        query = """
            SELECT f FROM Forecast f
            WHERE f.place.id = :place
            AND f.rate.category.id = :categoryId
            AND f.year = :year
            AND f.month >= :month
            """),
    @NamedQuery(
        name = "Forecast.findByPlaceIdAndYearAndBeforeMonth",
        query = """
            SELECT f FROM Forecast f
            WHERE f.place.id = :placeId
            AND f.year = :year
            AND f.month <= :month
            """),
    // place queries
    @NamedQuery(
        name = "Forecast.findByPlaceIdAndYear",
        query = """
            SELECT f FROM Forecast f
            WHERE f.place.id = :place
            AND f.year = :year
            ORDER BY f.rate.category.name ASC"""),
    @NamedQuery(
        name = "Forecast.findByPlaceIdAndYearAndMonth",
        query = """
            SELECT f FROM Forecast f
            WHERE f.place.id = :place
            AND f.month = :month
            AND f.year = :year
            ORDER BY f.rate.category.name ASC"""),
    @NamedQuery(
        name = "Forecast.findMinMonthByPlaceIdAndYear",
        query = """
            SELECT min(f.month) FROM Forecast f
            WHERE f.place.id = :id
            AND f.year = :year
            """),
    @NamedQuery(
        name = "Forecast.findByPlaceIdAndCategoryIdAndYearAndMonth",
        query = """
            SELECT f FROM Forecast f 
            WHERE f.place.id = :place
            AND f.rate.category.id = :categoryId
            AND f.month = :month 
            AND f.year = :year 
            ORDER BY f.rate.category.name ASC"""),
    // category queries
    @NamedQuery(
        name = "Forecast.findByYearAndCategoryName",
        query = """
            SELECT f FROM Forecast f
            WHERE f.year = :year AND f.rate.category.name = :categoryName"""),
    @NamedQuery(
        name = "Forecast.findByYearAndCategoryNameAndMonthBefore",
        query = """
            SELECT f FROM Forecast f
            WHERE
                f.year = :year
                AND f.rate.category.name = :categoryName
                AND f.month < :month"""),
    @NamedQuery(
        name = "Forecast.findByMonthAndYearAndCategoryId",
        query = """
            SELECT f FROM Forecast f
            WHERE f.month = :month AND f.year = :year AND f.rate.category.id = :categoryId"""),

    // rate queries
    @NamedQuery(
        name = "Forecast.findForecastYearsByPlaceId",
        query = "SELECT DISTINCT f.year FROM Forecast f WHERE f.place.id = :placeId ORDER BY f.year ASC"),
    @NamedQuery(
        name = "Forecast.findByBuildingIdAndYear",
        query = """
            SELECT f FROM Forecast f
            WHERE f.year = :year
            AND f.place.building.id = :buildingId
            """),
    @NamedQuery(
        name = "Forecast.findYearsAndMonths",
        query = """
            SELECT f.year AS year, f.month AS month
                FROM Forecast f
                GROUP BY f.year, f.month
                ORDER BY f.year, f.month
            """),
    @NamedQuery(
        name = "Forecast.findYearsAndMonthsByBuildingId",
        query = """
            SELECT f.year AS year, COUNT(DISTINCT f.month) AS months
                FROM Forecast f
                WHERE f.place.building.id = :id AND f.realValue IS NOT NULL
                GROUP BY f.year
                ORDER BY f.year
            """),
    @NamedQuery(
        name = "Forecast.findByBuildingIdAndYearAndMonth",
        query = """
                SELECT f FROM Forecast f
                    WHERE f.month = :month AND f.year = :year
                    AND f.place.building.id = :id
            """),
    @NamedQuery(
        name = "Forecast.deleteFutureForecastsByCategoryIdAndPlaceId",
        query = """
            DELETE FROM Forecast f
            WHERE f.rate.category.id = :categoryId
            AND f.place.id = :placeId
            AND f.year = :year
            AND f.month > :month
            """)
})
@EntityListeners({EntityControlListenerMOW.class})
public class Forecast extends AbstractEntity implements Serializable {

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
    @Column(name = "value", nullable = false, scale = 6, precision = 38)
    @Getter
    @Setter
    private BigDecimal value;
    @PositiveOrZero
    @Column(name = "real_value", scale = 6, precision = 38)
    @Getter
    @Setter
    private BigDecimal realValue = BigDecimal.ZERO;
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
    @JoinColumn(name = "rate_id", referencedColumnName = "id", nullable = false)
    @Getter
    @Setter
    private Rate rate;
    private static final long serialVersionUID = 1L;

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
