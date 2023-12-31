package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.math.BigDecimal;
import java.util.List;

@Local
public interface RateManagerLocal extends CommonManagerInterface {
    List<Rate> getCurrentRates() throws AppBaseException;

    void createRate(Rate newRate, Long categoryId) throws AppBaseException;

    void removeFutureRate(Long id) throws AppBaseException;

    void changeFutureRateValue(Long id, BigDecimal value) throws AppBaseException;
}
