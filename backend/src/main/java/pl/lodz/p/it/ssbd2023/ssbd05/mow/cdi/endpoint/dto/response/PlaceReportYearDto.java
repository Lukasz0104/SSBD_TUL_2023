package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaceReportYearDto {
    private Integer year;
    private BigDecimal forecastedCostSum;
    private BigDecimal totalCostSum;
    private List<PlaceCategoryReportYearDto> placeReportDtoList;
}
