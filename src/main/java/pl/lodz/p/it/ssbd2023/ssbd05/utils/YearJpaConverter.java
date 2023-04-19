package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Year;

@Converter(autoApply = true)
public class YearJpaConverter implements AttributeConverter<Year, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Year year) {
        if (year == null) {
            return null;
        }
        return year.getValue();
    }

    @Override
    public Year convertToEntityAttribute(Integer integer) {
        if (integer == null) {
            return null;
        }
        return Year.of(integer);
    }
}
