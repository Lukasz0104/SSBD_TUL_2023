package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.MeterDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MeterDtoConverter {

    @Inject
    private AppProperties appProperties;

    public MeterDto createMeterDtoFromMeter(Meter meter) {
        List<Reading> pastReliableReadings = meter.getReadings().stream()
            .filter(Reading::isReliable)
            .filter(reading -> reading.getDate().isBefore(LocalDateTime.now()))
            .sorted(Comparator.comparing(Reading::getDate).reversed())
            .toList();

        boolean hasReadingInLast30Days = false;
        LocalDateTime dateOfNextReading = LocalDateTime.now();
        if (pastReliableReadings.size() > 0) {
            LocalDateTime lastReadingDate = pastReliableReadings.get(0).getDate();
            hasReadingInLast30Days = lastReadingDate.isAfter(LocalDateTime.now().minusDays(30));
            dateOfNextReading = lastReadingDate.plusDays(appProperties.getDaysBetweenReadingsForOwner());
        }
        return new MeterDto(meter.getId(), meter.getCategory().getName(), hasReadingInLast30Days, dateOfNextReading);
    }

    public List<MeterDto> createMeterDtoListFromMeterList(Set<Meter> meters) {
        return meters.stream().map(this::createMeterDtoFromMeter).sorted(Comparator.comparing(MeterDto::getId))
            .toList();
    }
}
