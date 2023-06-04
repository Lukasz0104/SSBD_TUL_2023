package pl.lodz.p.it.ssbd2023.ssbd05.mow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ReportYearEntry {
    private BigDecimal predValue;
    private BigDecimal predAmount;
    private BigDecimal realValue;
    private BigDecimal realAmount;

    public ReportYearEntry() {
        predValue = new BigDecimal(0);
        predAmount = new BigDecimal(0);
        realValue = new BigDecimal(0);
        realAmount = new BigDecimal(0);
    }


    public ReportYearEntry(BigDecimal value, BigDecimal amount) {
        this.predValue = value;
        this.predAmount = amount;
        this.realValue = new BigDecimal(0);
        this.realAmount = new BigDecimal(0);
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

}