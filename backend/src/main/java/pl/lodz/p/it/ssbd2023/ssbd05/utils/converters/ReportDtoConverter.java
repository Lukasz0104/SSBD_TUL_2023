package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportYearlyDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportsDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ReportDtoConverter {

    public static BuildingReportYearlyDto mapToBuildingReportYearlyDto(Map<String, ReportYearEntry> categories) {
        BigDecimal sumPredValue = BigDecimal.ZERO;
        BigDecimal sumRealValue = BigDecimal.ZERO;
        for (Map.Entry<String,ReportYearEntry> entry: categories.entrySet()) {
            sumPredValue = sumPredValue.add(entry.getValue().getPredValue());
            sumRealValue = sumRealValue.add(entry.getValue().getRealValue());
        }
        return new BuildingReportYearlyDto(categories.values().stream().toList(),
            sumPredValue, sumRealValue, sumPredValue.subtract(sumRealValue));
    }

    public static List<BuildingReportsDto> mapToListOfBuildingReports(Map<Integer, List<Integer>> maps) {
        return maps.entrySet().stream().map(e -> new BuildingReportsDto(e.getKey(), e.getValue())).toList();
    }
}
