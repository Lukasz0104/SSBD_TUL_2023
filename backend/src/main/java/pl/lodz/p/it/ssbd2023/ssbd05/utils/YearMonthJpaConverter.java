package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthJpaConverter implements AttributeConverter<YearMonth, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(YearMonth yearMonth) {
        if (yearMonth == null) {
            return null;
        }
        return LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate date) {
        if (date == null) {
            return null;
        }
        return YearMonth.from(date);
    }
}
