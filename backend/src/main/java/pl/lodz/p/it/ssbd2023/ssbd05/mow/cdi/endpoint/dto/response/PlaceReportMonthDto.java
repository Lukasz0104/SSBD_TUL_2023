package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaceReportMonthDto {
    private Integer year;
    private Month month;
    private BigDecimal totalValue;
    private BigDecimal totalRealValue;
    private BigDecimal differential;
    private boolean completeMonth;
    private List<PlaceCategoryReportMonthDto> details;
    private BigDecimal balance;
}
