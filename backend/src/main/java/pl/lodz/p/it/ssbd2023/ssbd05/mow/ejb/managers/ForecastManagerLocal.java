package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

@Local
public interface ForecastManagerLocal extends CommonManagerInterface {
    void createCurrentForecast(Long placeId, Long categoryId, BigDecimal amount, String login) throws AppBaseException;

    void createForecastsForPlaceAndRateAndYear(Place place, Rate rate, Year year) throws AppBaseException;

    List<Integer> getForecastYearsByPlaceId(Long placeId);

    List<Integer> getForecastYearsByOwnPlaceId(Long placeId, String login) throws AppBaseException;

    Integer getOwnMinMonthFromForecast(Long placeId, Year year, String login) throws AppBaseException;

    Integer getMinMonthFromForecast(Long placeId, Year year) throws AppBaseException;
}
