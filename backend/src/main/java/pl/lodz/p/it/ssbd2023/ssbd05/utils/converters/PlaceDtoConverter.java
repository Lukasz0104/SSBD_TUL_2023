package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAddressDtoFromAddress;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.BuildingDtoConverter.mapBuildingToDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceOwnerDTO;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaceDtoConverter {

    public static PlaceCategoryDTO createPlaceCategoryDto(Rate rate) {
        return new PlaceCategoryDTO(rate.getId(), rate.getCategory().getName(), rate.getAccountingRule().toString(),
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

    public static PlaceOwnerDTO createPlaceOwnerDtoFromOwnerData(OwnerData ownerData) {
        Account account = ownerData.getAccount();
        return new PlaceOwnerDTO(
            ownerData.getId(),
            account.getFirstName(),
            account.getLastName(),
            createAddressDtoFromAddress(ownerData.getAddress()),
            ownerData.isActive());
    }
}
