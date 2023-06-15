package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.PlaceSignableDto;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto extends PlaceSignableDto {

    @NotNull @Positive
    private Integer placeNumber;

    @NotNull @Positive
    private BigDecimal squareFootage;

    @NotNull @Positive
    private Integer residentsNumber;

    @NotNull
    private boolean active;

    @NotNull @Valid
    private BuildingDto building;

    public PlaceDto(Long id, long version, Integer placeNumber, BigDecimal squareFootage,
                    Integer residentsNumber, boolean active, BuildingDto buildingDto) {
        super(id, version);
        this.placeNumber = placeNumber;
        this.squareFootage = squareFootage;
        this.residentsNumber = residentsNumber;
        this.active = active;
        this.building = buildingDto;
    }
}

