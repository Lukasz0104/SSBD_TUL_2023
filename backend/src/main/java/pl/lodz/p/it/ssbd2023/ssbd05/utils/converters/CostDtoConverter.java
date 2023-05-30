package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CostDto;

public class CostDtoConverter {

    public static CostDto createCostListDto(Cost cost) {
        return new CostDto(
            cost.getId(),
            cost.getVersion(),
            cost.getYear(),
            cost.getMonth(),
            cost.getTotalConsumption(),
            cost.getRealRate(),
            cost.getCategory().getName()
        );
    }
}
