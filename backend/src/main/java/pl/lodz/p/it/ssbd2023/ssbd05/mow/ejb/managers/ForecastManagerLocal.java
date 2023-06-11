package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.math.BigDecimal;
import java.util.List;

@Local
public interface ForecastManagerLocal extends CommonManagerInterface {
    void createCurrentForecast(Long placeId, Long categoryId, BigDecimal amount, String login) throws AppBaseException;

    List<Integer> getForecastYearsByPlaceId(Long placeId);

    List<Integer> getForecastYearsByOwnPlaceId(Long placeId, String login) throws AppBaseException;
}
