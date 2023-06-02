package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.MeterDto;

import java.util.Set;
import java.util.stream.Collectors;

public class MeterDtoConverter {
    public static MeterDto createMeterDtoFromMeter(Meter meter) {
        return new MeterDto(meter.getId(), meter.getCategory().getName());
    }

    public static Set<MeterDto> createMeterDtoListFromMeterList(Set<Meter> meters) {
        return meters.stream().map(MeterDtoConverter::createMeterDtoFromMeter).collect(Collectors.toSet());
    }
}
