package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.util.List;

@Local
public interface CostManagerLocal extends CommonManagerInterface {
    List<Cost> getAllCosts() throws AppBaseException;

    void createCost() throws AppBaseException;

    Cost getCostDetails(Long id) throws AppBaseException;

    void removeCost(Long id) throws AppBaseException;
}
