package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportYearlyDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportsDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ReportDtoConverter {

    public static BuildingReportYearlyDto mapToBuildingReportYearlyDto(Map<String, ReportYearEntry> categories) {
        BigDecimal sumPredValue = new BigDecimal(0);
        BigDecimal sumRealValue = new BigDecimal(0);
        for (Map.Entry<String,ReportYearEntry> entry: categories.entrySet()) {
            sumPredValue = sumRealValue.add(entry.getValue().getPredValue());
            sumRealValue = sumRealValue.add(entry.getValue().getRealValue());
        }
        return new BuildingReportYearlyDto(categories, sumPredValue, sumRealValue, sumPredValue.subtract(sumRealValue));
    }

    public static BuildingReportsDto mapToListOfBuildingReports(Map<Integer, List<Integer>> maps) {
        return new BuildingReportsDto(maps);
    }
}
