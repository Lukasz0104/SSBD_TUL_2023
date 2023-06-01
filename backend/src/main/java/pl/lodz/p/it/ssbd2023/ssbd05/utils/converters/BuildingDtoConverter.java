package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAddressDtoFromAddress;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Building;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BuildingDtoConverter {
    public static BuildingDto mapBuildingToDto(Building building) {
        return new BuildingDto(
            building.getId(),
            createAddressDtoFromAddress(building.getAddress()));
    }
}
