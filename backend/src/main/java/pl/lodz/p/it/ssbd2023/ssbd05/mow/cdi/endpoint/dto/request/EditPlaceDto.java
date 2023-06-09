package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPlaceDto {

    @NotNull
    private Long id;

    @NotNull @Positive
    private Long version;

    @NotNull @Positive
    private Integer placeNumber;

    @NotNull @Positive
    private BigDecimal squareFootage;

    @NotNull @Positive
    private Integer residentsNumber;

    @NotNull
    private boolean active;

}
