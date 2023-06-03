package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class BuildingReportYearlyDto {

    @Valid
    @NotNull
    private CategoryDTO category;

    @Valid
    @NotNull
    private BuildingDto building;



}
