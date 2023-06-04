package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingReportsDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YearInner {
        @PositiveOrZero
        private Integer year;
        private List<@Positive Integer> months;
    }

    @NotNull
    private List<@Valid YearInner> years;
}
