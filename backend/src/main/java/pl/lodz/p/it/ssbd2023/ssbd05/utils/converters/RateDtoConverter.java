package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.CreateRateDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RateDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RatePublicDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.util.List;

public class RateDtoConverter {
    public static RateDTO createRateDtoFromRate(Rate rate) {
        return new RateDTO(rate.getId(),
            rate.getCreatedTime(),
            (rate.getCreatedBy() != null) ? rate.getCreatedBy().getLogin() : "anonymous", rate.getUpdatedTime(),
            (rate.getUpdatedBy() != null) ? rate.getUpdatedBy().getLogin() : "anonymous",
            rate.getValue(), rate.getEffectiveDate(), rate.getAccountingRule().toString());
    }

    public static RatePublicDTO createPublicRateDtoFromRate(Rate rate) {
        return new RatePublicDTO(rate.getCategory().getName(), rate.getValue(), rate.getAccountingRule().toString());
    }

    public static Page<RateDTO> createRateDTOPage(Page<Rate> rates) {
        List<RateDTO> dtos = rates.getData().stream().map(RateDtoConverter::createRateDtoFromRate).toList();

        return new Page<>(dtos, rates.getTotalSize(), rates.getPageSize(), rates.getCurrentPage());
    }

    public static List<RatePublicDTO> createPublicRateDtoList(List<Rate> rates) {
        return rates.stream().map(RateDtoConverter::createPublicRateDtoFromRate).toList();
    }

    public static Rate createRateFromCreateRateDto(CreateRateDto createRateDto) {
        return new Rate(createRateDto.getValue(), AccountingRule.valueOf(createRateDto.getAccountingRule()),
            createRateDto.getEffectiveDate());
    }

}
