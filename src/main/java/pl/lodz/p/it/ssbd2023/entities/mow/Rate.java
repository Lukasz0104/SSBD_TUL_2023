package pl.lodz.p.it.ssbd2023.entities.mow;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.entities.AbstractEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "rate")
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Rate.findAll",
                query = "SELECT r FROM Rate r"),
        @NamedQuery(
                name = "Rate.findById",
                query = "SELECT r FROM Rate r WHERE r.id = :id"),
        @NamedQuery(
                name = "Rate.findCurrentRates",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate = (SELECT max(r1.effectiveDate) FROM Rate r1 WHERE r1.category = r.category)"),

        // accounting_rule queries
        @NamedQuery(
                name = "Rate.findByAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.accountingRule = :accounting_rule"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate = :effectiveDate AND r.accountingRule = :accounting_rule"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateBeforeAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate < :effectiveDate AND r.accountingRule = :accounting_rule"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAfterAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate >= :effectiveDate AND r.accountingRule = :accounting_rule"),

        // category queries
        @NamedQuery(
                name = "Rate.findByCategory",
                query = "SELECT r FROM Rate r WHERE r.category = :category"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAndCategory",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate = :effectiveDate AND r.category = :category"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateBeforeAndCategory",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate < :effectiveDate AND r.category = :category"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAfterAndCategory",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate >= :effectiveDate AND r.category = :category"),

        // mixed queries
        @NamedQuery(
                name = "Rate.findByCategoryAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.category = :category AND r.accountingRule = :accounting_rule"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAndCategoryAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate = :effectiveDate AND r.category = :category AND r.accountingRule = :accounting_rule"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateBeforeAndCategoryAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate < :effectiveDate AND r.category = :category AND r.accountingRule = :accounting_rule"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAfterAndCategoryAndAccountingRule",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate >= :effectiveDate AND r.category = :category AND r.accountingRule = :accounting_rule"),

        // only date queries
        @NamedQuery(
                name = "Rate.findByEffectiveDate",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate = :effectiveDate"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateBefore",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate < :effectiveDate"),
        @NamedQuery(
                name = "Rate.findByEffectiveDateAfter",
                query = "SELECT r FROM Rate r WHERE r.effectiveDate >= :effectiveDate"),
})
public class Rate extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Basic(optional = false)
    @Column(name = "value", nullable = false)
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
    @Basic(optional = false)
    @Column(name = "effective_date", nullable = false)
    @Getter
    @Setter
    private LocalDate effectiveDate;

    @NotNull
    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
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
