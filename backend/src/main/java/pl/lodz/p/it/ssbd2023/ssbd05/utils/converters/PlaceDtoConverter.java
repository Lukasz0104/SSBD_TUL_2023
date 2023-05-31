package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter.mapBuildingToDto;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDto;

public class PlaceDtoConverter {

    public static PlaceDto createPlaceDtoFromPlace(Place place) {
        return new PlaceDto(
            place.getPlaceNumber(),
            place.getSquareFootage(),
            place.getResidentsNumber(),
            place.isActive(),
            mapBuildingToDto(place.getBuilding())
        );
    }

}
