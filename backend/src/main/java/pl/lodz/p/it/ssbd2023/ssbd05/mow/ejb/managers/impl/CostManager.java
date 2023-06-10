package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.CostNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CostFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.CostManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.time.Year;
import java.util.List;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class CostManager extends AbstractManager implements CostManagerLocal, SessionSynchronization {

    @Inject
    private CostFacade costFacade;

    @Override
    public List<Cost> getAllCosts() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void createCost() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public Cost getCostDetails(Long id) throws AppBaseException {
        return costFacade.find(id).orElseThrow(CostNotFoundException::new);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void removeCost(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER})
    public Page<Cost> getAllCostsPage(int page, int pageSize, boolean order, Integer year,
                                      String categoryName) {
        return costFacade.findByYearAndMonthAndCategoryName(page,
            pageSize, order, Year.of(year), categoryName);
    }

    @Override
    @RolesAllowed({MANAGER})
    public List<String> getDistinctYearsFromCosts() {
        return costFacade.findDistinctYears().stream().map(Year::toString).toList();
    }

    @Override
    @RolesAllowed({MANAGER})
    public List<String> getDistinctCategoryNamesFromCosts() {
        return costFacade.findDistinctCategoryNamesFromCosts();
    }
}
