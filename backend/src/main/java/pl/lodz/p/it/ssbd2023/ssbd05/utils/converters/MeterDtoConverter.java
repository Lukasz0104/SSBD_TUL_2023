package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.MeterDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class MeterDtoConverter {
    public static MeterDto createMeterDtoFromMeter(Meter meter) {
        boolean hasReadingInLast30Days = meter.getReadings().stream()
            .anyMatch(reading -> reading.getDate().isAfter(LocalDateTime.now().minusDays(30)));
        
        return new MeterDto(meter.getId(), meter.getCategory().getName(), hasReadingInLast30Days);
    }

    public static Set<MeterDto> createMeterDtoListFromMeterList(Set<Meter> meters) {
        return meters.stream().map(MeterDtoConverter::createMeterDtoFromMeter).collect(Collectors.toSet());
    }
}
