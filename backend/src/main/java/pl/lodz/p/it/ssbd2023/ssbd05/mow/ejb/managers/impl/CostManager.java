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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.CategoryNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.CostNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CategoryFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CostFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.CostManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
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

    @Inject
    private PlaceFacade placeFacade;

    @Inject
    private CategoryFacade categoryFacade;

    @Inject
    private ForecastFacade forecastFacade;

    @Override
    public List<Cost> getAllCosts() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER})
    public void createCost(Year year,
                           Month month,
                           BigDecimal totalConsumption,
                           BigDecimal realRate,
                           Long categoryId) throws AppBaseException {

        Category category = categoryFacade.find(categoryId).orElseThrow(CategoryNotFoundException::new);

        Cost newCost = new Cost(year, month, totalConsumption, realRate, category);

        costFacade.create(newCost);

        List<Place> placeList = placeFacade.findAll(); // optimise? swap place with forecast

        for (Place place : placeList) {
            Forecast placeForecast = forecastFacade.findByPlaceIdAndCategoryIdAndYearAndMonth(place.getId(), categoryId,
                year, month).orElse(null);
            if (placeForecast == null) {
                continue;
            }

            List<Forecast> forecastList = forecastFacade.findByMonthAndYearAndCategory(month, year, categoryId);

            BigDecimal totalForecastedConsumption =
                forecastList.stream().map(Forecast::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthlyShares =
                placeForecast.getAmount().divide(totalForecastedConsumption, 6, RoundingMode.CEILING);

            BigDecimal realValue = monthlyShares.multiply(newCost.getTotalConsumption()
                .multiply(newCost.getRealRate()));

            placeForecast.setRealValue(realValue);

            forecastFacade.edit(placeForecast);

            BigDecimal oldMapBalance = place.getBalance()
                .getOrDefault(YearMonth.of(year.getValue(), month), BigDecimal.ZERO);

            BigDecimal currentMapBalance = oldMapBalance.add(realValue.subtract(placeForecast.getValue()));

            for (int j = month.getValue(); j < 13; j++) {
                place.getBalance().put(YearMonth.of(year.getValue(), Month.of(j)),
                    currentMapBalance);
            }

            placeFacade.edit(place);
        }
    }

    @Override
    @RolesAllowed(MANAGER)
    public Cost getCostDetails(Long id) throws AppBaseException {
        return costFacade.find(id).orElseThrow(CostNotFoundException::new);
    }

    @Override
    @RolesAllowed({MANAGER})
    public void removeCost(Long id) throws AppBaseException {
        Cost cost = costFacade.find(id).orElseThrow(CostNotFoundException::new);

        List<Forecast> forecastList = forecastFacade.findByMonthAndYearAndCategory(
            cost.getMonth(), cost.getYear(), cost.getCategory().getId());

        for (Forecast forecast : forecastList) {
            forecast.setRealValue(BigDecimal.ZERO);
            forecastFacade.edit(forecast);
        }
        costFacade.remove(cost);
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
