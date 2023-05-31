package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.MeterDto;

import java.util.List;

public class MeterDtoConverter {
    public static MeterDto createMeterDtoFromMeter(Meter meter) {
        return new MeterDto(meter.getId(), meter.getCategory().getName());
    }

    public static List<MeterDto> createMeterDtoListFromMeterList(List<Meter> meters) {
        return meters.stream().map(MeterDtoConverter::createMeterDtoFromMeter).toList();
    }
}
