package pl.lodz.p.it.ssbd2023.ssbd05.entities.mow;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.EntityControlListenerMOW;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rate", uniqueConstraints = {
    @UniqueConstraint(name = "unq_rate_0", columnNames = {"effective_date", "category_id"})})
@NoArgsConstructor
@NamedQueries({
    @NamedQuery(
        name = "Rate.findCurrentRates",
        query = """
            SELECT r FROM Rate r
            WHERE r.effectiveDate =
                (SELECT max(r1.effectiveDate)
                 FROM Rate r1 WHERE r1.effectiveDate <= CURRENT_DATE AND r1.category = r.category)
            """),
    @NamedQuery(
        name = "Rate.findCurrentRateByCategoryId",
        query = """
            SELECT r FROM Rate r
            WHERE r.effectiveDate =
                (SELECT max(r1.effectiveDate)
                 FROM Rate r1 WHERE r1.effectiveDate <= CURRENT_DATE AND r1.category = r.category)
            AND r.category.id = :categoryId
            """),
    @NamedQuery(
        name = "Rate.findByYearAndCategoryId",
        query = """
            SELECT r FROM Rate r
            WHERE r.category.id = :categoryId AND EXTRACT(YEAR FROM r.effectiveDate) = :year
            ORDER BY r.effectiveDate DESC"""),
    @NamedQuery(
        name = "Rate.findByCategoryId",
        query = "SELECT r FROM Rate r WHERE r.category.id = :categoryId ORDER BY r.effectiveDate DESC"),
    @NamedQuery(
        name = "Rate.countByCategoryId",
        query = "SELECT count(r.id) FROM Rate r WHERE r.category.id = :categoryId"),

    // only date queries
    // other
    @NamedQuery(
        name = "Rate.findByEffectiveDateBeforeOrderedByDate",
        query = """
            SELECT r FROM Rate r
            WHERE r.effectiveDate >= :effectiveDate AND r.category.id = :categoryId
            ORDER BY r.effectiveDate ASC""")
})
@EntityListeners({EntityControlListenerMOW.class})
public class Rate extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @PositiveOrZero
    @NotNull
    @Basic(optional = false)
    @Column(name = "value", nullable = false, scale = 2, precision = 38)
    @Getter
    @Setter
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Basic(optional = false)
    @Column(name = "accounting_rule", nullable = false)
    @Getter
    @Setter
    private AccountingRule accountingRule;

    @NotNull
    @Future
    @Basic(optional = false)
    @Column(name = "effective_date", nullable = false)
    @Getter
    @Setter
    private LocalDate effectiveDate;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", referencedColumnName = "id", updatable = false, nullable = false)
    @Getter
    @Setter
    private Category category;

    public Rate(BigDecimal value, AccountingRule accountingRule, LocalDate effectiveDate) {
        this.value = value;
        this.accountingRule = accountingRule;
        this.effectiveDate = effectiveDate;
    }
}
