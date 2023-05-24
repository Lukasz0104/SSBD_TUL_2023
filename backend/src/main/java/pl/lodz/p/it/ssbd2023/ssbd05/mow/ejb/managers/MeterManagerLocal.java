package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.util.List;

@Local
public interface MeterManagerLocal extends CommonManagerInterface {
    List<Reading> getMeterReadings(Long id) throws AppBaseException;
}
