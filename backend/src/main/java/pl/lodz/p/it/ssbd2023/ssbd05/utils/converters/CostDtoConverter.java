package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CostDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.util.List;

public class CostDtoConverter {

    public static CostDto createCostDto(Cost cost) {
        return new CostDto(
            cost.getId(),
            cost.getVersion(),
            cost.getYear().getValue(),
            cost.getMonth(),
            cost.getTotalConsumption(),
            cost.getRealRate(),
            cost.getCategory().getName()
        );
    }

    public static Page<CostDto> createCostDtoPage(Page<Cost> costPage) {
        List<CostDto> costDtoList = costPage.getData().stream().map(CostDtoConverter::createCostDto).toList();
        return new Page<>(costDtoList, costPage.getTotalSize(), costPage.getPageSize(), costPage.getCurrentPage());
    }
}
