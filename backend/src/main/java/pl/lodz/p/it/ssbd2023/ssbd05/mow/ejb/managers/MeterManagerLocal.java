package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

@Local
public interface MeterManagerLocal extends CommonManagerInterface {
    Page<Reading> getMeterReadingsAsOwner(Long id, String login, int page, int pageSize) throws AppBaseException;

    Page<Reading> getMeterReadingsAsManager(Long id, int page, int pageSize) throws AppBaseException;
}
