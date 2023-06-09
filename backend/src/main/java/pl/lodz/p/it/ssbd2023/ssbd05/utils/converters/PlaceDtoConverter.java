package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter.mapBuildingToDto;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDTO;

import java.util.ArrayList;
import java.util.List;

public class PlaceDtoConverter {

    public static PlaceCategoryDTO createPlaceCategoryDto(Rate rate) {
        return new PlaceCategoryDTO(rate.getCategory().getId(), rate.getId(), rate.getCategory().getName(),
            rate.getAccountingRule().toString(),
            rate.getValue());
    }

    public static List<PlaceCategoryDTO> createPlaceCategoryDtoList(List<Rate> rateList) {
        List<PlaceCategoryDTO> placeCategoryDtosList = new ArrayList<>();
        for (Rate r : rateList) {
            placeCategoryDtosList.add(createPlaceCategoryDto(r));
        }
        return placeCategoryDtosList;
    }

    public static PlaceDTO createPlaceDtoFromPlace(Place place) {
        return new PlaceDTO(
            place.getId(),
            place.getPlaceNumber(),
            place.getSquareFootage(),
            place.getResidentsNumber(),
            place.isActive(),
            mapBuildingToDto(place.getBuilding())
        );
    }


    public static List<PlaceDTO> createPlaceDtoList(List<Place> places) {
        return places.stream()
            .map(PlaceDtoConverter::createPlaceDtoFromPlace)
            .toList();
    }
}
