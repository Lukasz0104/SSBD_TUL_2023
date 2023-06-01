package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlaceDto {
    @NotNull
    private Long id;

    @NotNull
    private Integer placeNumber;

    @NotNull
    @Valid
    private AddressDto addressDto;
}
