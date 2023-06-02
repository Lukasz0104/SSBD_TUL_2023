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
@Table(name = "cost", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "month", "category_id"}))
@NamedQueries({
    @NamedQuery(
        name = "Cost.findAll",
        query = "SELECT c FROM Cost c"),
    @NamedQuery(
        name = "Cost.findById",
        query = "SELECT c FROM Cost c WHERE c.id = :id"),
    @NamedQuery(
        name = "Cost.findByYear",
        query = "SELECT c FROM Cost c WHERE c.year = :year"),
    @NamedQuery(
        name = "Cost.findByMonth",
        query = "SELECT c FROM Cost c WHERE c.month = :month"),
    @NamedQuery(
        name = "Cost.findByCategoryId",
        query = "SELECT c FROM Cost c WHERE c.category.id = :categoryId"),
    @NamedQuery(
        name = "Cost.findByCategoryName",
        query = "SELECT c FROM Cost c WHERE c.category.name = :categoryName"),
    @NamedQuery(
        name = "Cost.findByYearAndMonth",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND c.month = :month"""),
    @NamedQuery(
        name = "Cost.findByYearAndCategoryId",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND c.category.id = :categoryId"""),
    @NamedQuery(
        name = "Cost.findByYearAndCategoryName",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND c.category.name = :categoryName"""),
    @NamedQuery(
        name = "Cost.findByMonthAndCategoryId",
        query = """
            SELECT c FROM Cost c
            WHERE c.month = :month
                  AND c.category.id = :categoryId"""),
    @NamedQuery(
        name = "Cost.findByMonthAndCategoryName",
        query = """
            SELECT c FROM Cost c
            WHERE c.month = :month
                  AND c.category.name = :categoryName"""),
    @NamedQuery(
        name = "Cost.findByYearAndMonthAndCategoryId",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND c.month = :month
                  AND c.category.id = :categoryId"""),
    @NamedQuery(
        name = "Cost.findByYearAndMonthAndCategoryName",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND c.month = :month
                  AND c.category.name = :categoryName"""),
    @NamedQuery(
        name = "Cost.findByYearAndCategoryNameAsc",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND LOWER(c.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))
                  ORDER BY c.month ASC"""),
    @NamedQuery(
        name = "Cost.findByYearAndCategoryNameDesc",
        query = """
            SELECT c FROM Cost c
            WHERE c.year = :year
                  AND LOWER(c.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))
                  ORDER BY c.month DESC"""),
    @NamedQuery(
        name = "Cost.countByYearAndCategoryName",
        query = """
            SELECT COUNT(c) FROM Cost c
            WHERE c.year = :year
                  AND LOWER(c.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))"""),
    @NamedQuery(
        name = "Cost.findDistinctYears",
        query = """
            SELECT DISTINCT c.year FROM Cost c ORDER BY c.year"""),
    @NamedQuery(
        name = "Cost.findDistinctCategoryNames",
        query = """
            SELECT DISTINCT c.category.name FROM Cost c""")

})
@NoArgsConstructor
@EntityListeners({EntityControlListenerMOW.class})
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

    @PositiveOrZero
    @Column(name = "total_consumption", scale = 3, precision = 38)
    @Getter
    @Setter
    private BigDecimal totalConsumption;

    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = "real_rate", nullable = false, scale = 2, precision = 38)
    @Getter
    @Setter
    private BigDecimal realRate;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    public Cost(Year year, Month month, BigDecimal totalConsumption, BigDecimal realRate,
                Category category) {
        this.year = year;
        this.month = month;
        this.totalConsumption = totalConsumption;
        this.realRate = realRate;
        this.category = category;
    }
}
