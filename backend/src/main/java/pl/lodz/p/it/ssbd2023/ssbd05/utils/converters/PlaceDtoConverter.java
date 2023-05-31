package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryDto;

import java.util.ArrayList;
import java.util.List;

public class PlaceDtoConverter {

    public static PlaceCategoryDto createPlaceCategoryDto(Rate rate) {
        return new PlaceCategoryDto(rate.getId(), rate.getCategory().getName(), rate.getAccountingRule().toString(),
            rate.getValue());
    }

    public static List<PlaceCategoryDto> createPlaceCategoryDtoList(List<Rate> rateList) {
        List<PlaceCategoryDto> placeCategoryDtosList = new ArrayList<>();
        for (Rate r : rateList) {
            placeCategoryDtosList.add(createPlaceCategoryDto(r));
        }
        return placeCategoryDtosList;
    }
}
