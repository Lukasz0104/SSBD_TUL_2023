package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

@Local
public interface ReadingManagerLocal extends CommonManagerInterface {
    void createReadingAsOwner(Reading reading, Long meterId, String login) throws AppBaseException;

    void createReadingAsManager(Reading reading, Long meterId, String login) throws AppBaseException;
}
