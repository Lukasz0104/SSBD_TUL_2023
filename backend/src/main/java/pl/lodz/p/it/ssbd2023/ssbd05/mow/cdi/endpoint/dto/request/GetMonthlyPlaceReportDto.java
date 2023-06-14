package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetMonthlyPlaceReportDto {

    @NotNull
    Long id;

    @NotNull
    @Min(2020)
    @Max(2999)
    Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    Integer month;

    boolean full = false;
}
