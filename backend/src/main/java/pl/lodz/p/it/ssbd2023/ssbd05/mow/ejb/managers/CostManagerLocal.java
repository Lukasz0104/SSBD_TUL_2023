package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Local
public interface CostManagerLocal extends CommonManagerInterface {
    List<Cost> getAllCosts() throws AppBaseException;

    void createCost(Year year,
                    Month month,
                    BigDecimal totalConsumption,
                    BigDecimal realRate,
                    Long categoryId) throws AppBaseException;

    Cost getCostDetails(Long id) throws AppBaseException;

    void removeCost(Long id) throws AppBaseException;

    Page<Cost> getAllCostsPage(int page, int pageSize, boolean order, Integer year,
                               String categoryName) throws AppBaseException;

    List<String> getDistinctYearsFromCosts() throws AppBaseException;

    List<String> getDistinctCategoryNamesFromCosts() throws AppBaseException;

}
