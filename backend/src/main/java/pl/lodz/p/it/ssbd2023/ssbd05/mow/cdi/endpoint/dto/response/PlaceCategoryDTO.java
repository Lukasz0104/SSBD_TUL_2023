package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceCategoryDTO {

    @NotNull
    private Long categoryId;

    @NotNull
    private Long rateId;

    @NotBlank
    private String categoryName;

    @NotBlank
    private String accountingRule;

    @NotNull
    private BigDecimal rate;

    @NotNull
    private LocalDate effectiveDate;
}
