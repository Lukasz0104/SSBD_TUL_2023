package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.PlaceSignableDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDto;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPlaceDto extends PlaceSignableDto {

    @NotNull @Positive @Max(1000000)
    private Integer placeNumber;

    @NotNull @Positive @Max(1000000)
    private BigDecimal squareFootage;

    @NotNull @Positive @Max(1000000)
    private Integer residentsNumber;

    @NotNull
    private boolean active;

    public EditPlaceDto(PlaceDto place) {
        super(place.getId(), place.getVersion());
        this.placeNumber = place.getPlaceNumber();
        this.squareFootage = place.getSquareFootage();
        this.residentsNumber = place.getResidentsNumber();
        this.active = place.isActive();
    }

    public EditPlaceDto(long id, long version, Integer placeNumber,
                        BigDecimal squareFootage, Integer residentsNumber, boolean active) {
        super(id, version);
        this.placeNumber = placeNumber;
        this.squareFootage = squareFootage;
        this.residentsNumber = residentsNumber;
        this.active = active;
    }
}
