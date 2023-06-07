package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddReadingAsManagerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.AddReadingAsOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.ReadingDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.time.LocalDateTime;
import java.util.List;

public class ReadingDtoConverter {

    public static ReadingDto createDtoFromReading(Reading reading) {
        return new ReadingDto(reading.getCreatedTime(),
            (reading.getCreatedBy() != null) ? reading.getCreatedBy().getLogin() : "anonymous",
            reading.getUpdatedTime(),
            (reading.getUpdatedBy() != null) ? reading.getUpdatedBy().getLogin() : "anonymous", reading.getId(),
            reading.getDate(), reading.getValue(), reading.isReliable());
    }

    public static Reading createReadingFromDto(AddReadingAsOwnerDto dto) {
        return new Reading(LocalDateTime.now(), dto.getValue());
    }

    public static Reading createReadingFromDto(AddReadingAsManagerDto dto) {
        return new Reading(dto.getDate() != null ? dto.getDate().atStartOfDay() : LocalDateTime.now(), dto.getValue());
    }

    public static Page<ReadingDto> createReadingDtoPage(Page<Reading> readings) {
        List<ReadingDto> readingDTOs = readings.getData().stream()
            .map(ReadingDtoConverter::createDtoFromReading)
            .toList();

        return new Page<>(readingDTOs, readings.getTotalSize(), readings.getPageSize(),
            readings.getCurrentPage());
    }
}
