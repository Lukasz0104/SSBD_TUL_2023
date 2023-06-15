package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import lombok.Getter;
import lombok.Setter;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommunityReportDto {
    private BigDecimal balance;
    private final List<ReportYearEntry> reportsPerCategory = new ArrayList<>();

    public CommunityReportDto(BigDecimal balance) {
        this.balance = balance;
    }

    public CommunityReportDto(BigDecimal balance, List<ReportYearEntry> reportsPerCategory) {
        this.balance = balance;
        this.reportsPerCategory.addAll(reportsPerCategory);
    }

    public void addReport(ReportYearEntry reportEntry) {
        this.reportsPerCategory.add(reportEntry);
    }
}
