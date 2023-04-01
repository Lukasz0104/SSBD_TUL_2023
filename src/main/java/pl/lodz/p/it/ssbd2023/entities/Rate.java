package pl.lodz.p.it.ssbd2023.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rate")
@NoArgsConstructor
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
    private LocalDateTime effectiveDate;

    public Rate(BigDecimal value, AccountingRule accountingRule, LocalDateTime effectiveDate) {
        this.value = value;
        this.accountingRule = accountingRule;
        this.effectiveDate = effectiveDate;
    }
}
