package pl.lodz.p.it.ssbd2023.ssbd05.mow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportYearEntry {
    private BigDecimal predValue;
    private BigDecimal predAmount;
    private BigDecimal realValue;
    private BigDecimal realAmount;
    private AccountingRule accountingRule;
    private BigDecimal rate;
    private BigDecimal balance;
    private String categoryName;

    public ReportYearEntry(BigDecimal rate, AccountingRule accountingRule, String categoryName) {
        predValue = BigDecimal.ZERO;
        predAmount = BigDecimal.ZERO;
        realValue = BigDecimal.ZERO;
        realAmount = BigDecimal.ZERO;
        this.rate = rate;
        this.accountingRule = accountingRule;
        this.categoryName = categoryName;
    }

    public ReportYearEntry addPred(BigDecimal predValue, BigDecimal predAmount) {
        this.predValue = this.predValue.add(predValue);
        this.predAmount = this.predAmount.add(predAmount);
        return this;
    }

    public ReportYearEntry addReal(BigDecimal realValue, BigDecimal realAmount) {
        this.realValue = this.realValue.add(realValue);
        this.realAmount = this.realAmount.add(realAmount);
        return this;
    }

    public ReportYearEntry addMonth(BigDecimal predValue, BigDecimal predAmount, BigDecimal realValue) {
        this.predValue = this.predValue.add(predValue);
        this.predAmount = this.predAmount.add(predAmount);
        this.realValue = this.realValue.add(realValue);
        return this;
    }

}