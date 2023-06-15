package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Category;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Cost;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Forecast;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppInternalServerErrorException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.InaccessibleReportException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.BuildingNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.PlaceNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ReportYearEntry;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.BuildingReportYearlyDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CommunityReportDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.BuildingFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CategoryFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.CostFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ForecastFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.RateFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReadingFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReportFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.ReportManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ReportPlaceForecastMonth;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.ReportPlaceForecastYear;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.ReportDtoConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class ReportManager extends AbstractManager implements ReportManagerLocal, SessionSynchronization {

    @Inject
    private ReportFacade reportFacade;

    @Inject
    private PlaceFacade placeFacade;

    @Inject
    private ForecastFacade forecastFacade;

    @Inject
    private CategoryFacade categoryFacade;

    @Inject
    private CostFacade costFacade;

    @Inject
    private RateFacade rateFacade;

    @Inject
    private ReadingFacade readingFacade;

    @Inject
    private BuildingFacade buildingFacade;

    @Override
    @RolesAllowed({MANAGER, OWNER})
    public CommunityReportDto getReportDetails(Integer year, Integer month) throws AppBaseException {
        var yearMonth = YearMonth.of(year, month);
        var balance = placeFacade.sumBalanceForMonthAndYearAcrossAllPlaces(yearMonth);
        var dto = new CommunityReportDto(balance);

        var categories = categoryFacade.findAll();
        var monthObj = Month.of(month);
        var yearObj = Year.of(year);
        for (var category : categories) {
            var forecasts = forecastFacade.findByMonthAndYearAndCategory(monthObj, yearObj, category.getId());
            if (forecasts.isEmpty()) {
                continue;
            }
            var rate = forecasts.get(0).getRate();
            var rye = new ReportYearEntry(rate.getValue(), rate.getAccountingRule(), category.getName());
            forecasts.forEach(f -> rye.addPred(f.getValue(), f.getAmount()));

            if (yearMonth.isBefore(YearMonth.from(LocalDateTime.now()))) {
                rye.setRealAmount(costFacade.sumConsumptionForCategoryAndMonth(yearObj, category.getId(), monthObj));

                rye.setRealValue(forecasts.stream()
                    .map(Forecast::getRealValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            dto.addReport(rye);
        }
        return dto;
    }

    @Override
    @RolesAllowed(MANAGER)
    public Map<Integer, List<Integer>> getAllCommunityReportsYearsAndMonths() throws AppBaseException {
        return forecastFacade.findYearsAndMonths();
    }

    @Override
    @RolesAllowed(MANAGER)
    public CommunityReportDto getCommunityReportByYear(Integer year) throws AppBaseException {
        var yearObject = Year.of(year);
        var reports = reportFacade.findByYear(yearObject);

        List<ReportYearEntry> reportEntries;
        BigDecimal balance;
        if (!reports.isEmpty()) {
            reportEntries = getCommunityReportForYearWithReports(yearObject, reports);
            balance = placeFacade.sumBalanceForMonthAndYearAcrossAllPlaces(YearMonth.of(year, 12));
        } else {
            reportEntries = calculateCommunityReportForOngoingYear(yearObject);
            int month = reportEntries.isEmpty() ? 1 : reportEntries.size();
            balance = placeFacade.sumBalanceForMonthAndYearAcrossAllPlaces(YearMonth.of(year, month));
        }
        return new CommunityReportDto(balance, reportEntries);
    }

    private List<ReportYearEntry> getCommunityReportForYearWithReports(Year year, List<Report> reports) {
        Map<String, List<Report>> reportsGroupedByCategoryName = reports
            .stream()
            .collect(Collectors.groupingBy(r -> r.getCategory().getName()));

        List<ReportYearEntry> yearlyCommunityReports = new ArrayList<>(reportsGroupedByCategoryName.size());

        for (Map.Entry<String, List<Report>> entry : reportsGroupedByCategoryName.entrySet()) {
            List<Forecast> forecasts = forecastFacade.findByYearAndCategoryName(year, entry.getKey());

            BigDecimal averageRate = forecasts.stream()
                .map(Forecast::getRate)
                .map(Rate::getValue)
                .collect(
                    Collectors.teeing(
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add),
                        Collectors.counting(),
                        (sum, count) -> sum.divide(BigDecimal.valueOf(count), 6, RoundingMode.CEILING)
                    )
                );

            ReportYearEntry rye = new ReportYearEntry(
                averageRate,
                forecasts.get(0).getRate().getAccountingRule(),
                entry.getKey());

            forecasts.forEach(f -> rye.addMonth(f.getValue(), f.getAmount(), f.getRealValue()));

            rye.setRealAmount(entry.getValue()
                .stream()
                .map(Report::getTotalConsumption)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

            yearlyCommunityReports.add(rye);
        }

        return yearlyCommunityReports;
    }

    private List<ReportYearEntry> calculateCommunityReportForOngoingYear(Year year) {
        Month lastMonth = LocalDateTime.now().minusMonths(1).getMonth();
        List<ReportYearEntry> yearlyCommunityReports = new ArrayList<>(12);

        var categories = categoryFacade.findAll();

        for (var category : categories) {
            var forecasts = forecastFacade.findByYearAndCategoryNameAndMonthBefore(year, category.getName(), lastMonth);

            if (forecasts.isEmpty()) {
                continue;
            }

            BigDecimal averageForecastedRate = forecasts.stream()
                .map(Forecast::getRate)
                .map(Rate::getValue)
                .collect(
                    Collectors.teeing(
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add),
                        Collectors.counting(),
                        (sum, count) -> sum.divide(BigDecimal.valueOf(count), 6, RoundingMode.CEILING)
                    )
                );

            ReportYearEntry rye = new ReportYearEntry(
                averageForecastedRate,
                forecasts.get(0).getRate().getAccountingRule(),
                category.getName());

            forecasts.forEach(f -> rye.addMonth(f.getValue(), f.getAmount(), f.getRealValue()));

            var consumption = costFacade.sumConsumptionForCategoryAndMonthBefore(year, category.getId(), lastMonth);

            rye.setRealAmount(consumption);
            yearlyCommunityReports.add(rye);
        }

        return yearlyCommunityReports;
    }

    @Override
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public Map<Integer, List<Integer>> getYearsAndMonthsForReports(Long id) throws AppBaseException {
        buildingFacade.find(id).orElseThrow(BuildingNotFoundException::new);
        return forecastFacade
            .findYearsAndMonthsByBuildingId(id)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> IntStream.rangeClosed(1, e.getValue()).boxed().toList()
            ));
    }

    @Override
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public BuildingReportYearlyDto getYearlyReportForBuilding(Long id, Year year) throws AppBaseException {
        buildingFacade.find(id).orElseThrow(BuildingNotFoundException::new);
        List<Report> reports = reportFacade.findByYear(year);
        if (reports.isEmpty()) {
            return this.getUnfullYearlyReportForBuilding(id, year);
        }
        return this.getFullYearlyReportForBuilding(id, year);
    }

    private BuildingReportYearlyDto getUnfullYearlyReportForBuilding(Long id, Year year) throws AppBaseException {
        Month lastMonth = LocalDateTime.now().getMonth().minus(1);
        Map<String, ReportYearEntry> result = new HashMap<>();
        List<Place> places = placeFacade.findByBuildingId(id);
        YearMonth yearMonth = YearMonth.of(year.getValue(), lastMonth);
        BigDecimal balance = places
            .stream()
            .filter((v) -> v.getBalance().containsKey(yearMonth))
            .map(v -> v.getBalance().get(yearMonth))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Forecast> forecasts = forecastFacade.findByBuildingIdAndYear(id, year);
        for (Forecast forecast : forecasts) {
            String cat = forecast.getRate().getCategory().getName();
            BigDecimal realValue = forecast.getRealValue();
            if (realValue == null) {
                realValue = BigDecimal.ZERO;
            }
            result.put(cat,
                result.getOrDefault(cat, new ReportYearEntry(forecast.getRate().getValue(),
                        forecast.getRate().getAccountingRule(), cat))
                    .addPred(forecast.getValue(), forecast.getAmount())
                    .addReal(realValue, BigDecimal.ZERO)
            );
        }

        return ReportDtoConverter.mapToBuildingReportYearlyDto(result, balance);
    }

    private BuildingReportYearlyDto getFullYearlyReportForBuilding(Long id, Year year) throws AppBaseException {
        Map<String, ReportYearEntry> result = new HashMap<>();
        List<Forecast> forecasts = forecastFacade.findByBuildingIdAndYear(id, year);
        List<Report> reports = reportFacade.findByBuildingIdAndYear(id, year);
        List<Place> places = placeFacade.findByBuildingId(id);
        YearMonth yearMonth = YearMonth.of(year.getValue(), 12);
        BigDecimal balance = places
            .stream()
            .filter((v) -> v.getBalance().containsKey(yearMonth))
            .map(v -> v.getBalance().get(yearMonth))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Forecast forecast : forecasts) {
            String cat = forecast.getRate().getCategory().getName();
            result.put(cat,
                result.getOrDefault(cat, new ReportYearEntry(forecast.getRate().getValue(),
                        forecast.getRate().getAccountingRule(), cat))
                    .addPred(forecast.getValue(), forecast.getAmount())
            );
        }

        for (Report report : reports) {
            String cat = report.getCategory().getName();
            try {
                result.put(cat,
                    result.get(cat).addReal(report.getTotalCost(), report.getTotalConsumption())
                );
            } catch (NullPointerException e) {
                throw new AppInternalServerErrorException();
            }
        }

        return ReportDtoConverter.mapToBuildingReportYearlyDto(result, balance);
    }

    @Override
    @RolesAllowed({MANAGER, OWNER, ADMIN})
    public BuildingReportYearlyDto getMonthlyReportForBuilding(Long id, Year year, Month month)
        throws AppBaseException {
        buildingFacade.find(id).orElseThrow(BuildingNotFoundException::new);
        List<Place> places = placeFacade.findByBuildingId(id);
        YearMonth yearMonth = YearMonth.of(year.getValue(), month);
        BigDecimal balance = places
            .stream()
            .filter((v) -> v.getBalance().containsKey(yearMonth))
            .map(v -> v.getBalance().get(yearMonth))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, ReportYearEntry> result = new HashMap<>();
        List<Forecast> forecasts = forecastFacade.findByBuildingIdAndYearAndMonth(id, year, month);

        for (Forecast forecast : forecasts) {
            String cat = forecast.getRate().getCategory().getName();
            BigDecimal realValue = forecast.getRealValue();
            if (realValue == null) {
                realValue = BigDecimal.ZERO;
            }
            result.put(cat,
                result.getOrDefault(cat, new ReportYearEntry(forecast.getRate().getValue(),
                        forecast.getRate().getAccountingRule(), cat))
                    .addMonth(forecast.getValue(), forecast.getAmount(), realValue)
            );
        }
        return ReportDtoConverter.mapToBuildingReportYearlyDto(result, balance);
    }

    @Override
    @RolesAllowed(MANAGER)
    public ReportPlaceForecastYear getAllReportsDataByPlaceAndYear(Long placeId, Year year)
        throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        return new ReportPlaceForecastYear(
            reportFacade.findByPlaceIdAndYear(placeId, year),
            forecastFacade.findByPlaceIdAndYear(placeId, year),
            place.getBalance().get(YearMonth.of(year.getValue(), 12)));

    }

    @Override
    @RolesAllowed(OWNER)
    public ReportPlaceForecastYear getAllOwnReportsDataByPlaceAndYear(Long placeId, Year year, String login)
        throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        checkUserPlace(place, login);
        return new ReportPlaceForecastYear(
            reportFacade.findByPlaceIdAndYear(placeId, year),
            forecastFacade.findByPlaceIdAndYear(placeId, year),
            place.getBalance().get(YearMonth.of(year.getValue(), 12)));
    }

    @Override
    @RolesAllowed(MANAGER)
    public ReportPlaceForecastMonth getAllReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month,
                                                                            boolean full)
        throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        return getMonthlyReport(
            placeId,
            year,
            month,
            place.getBalance().get(
                YearMonth.of(
                    year.getValue(),
                    month.getValue())), full);
    }

    @Override
    @RolesAllowed(OWNER)
    public ReportPlaceForecastMonth getAllOwnReportsDataByPlaceAndYearAndMonth(Long placeId, Year year, Month month,
                                                                               String login, boolean full)
        throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        checkUserPlace(place, login);
        return getMonthlyReport(
            placeId,
            year,
            month,
            place.getBalance().get(
                YearMonth.of(
                    year.getValue(),
                    month.getValue())), full);
    }

    @RolesAllowed({OWNER, MANAGER})
    private ReportPlaceForecastMonth getMonthlyReport(Long placeId, Year year, Month month, BigDecimal balance,
                                                      boolean full) {
        if (full) {
            return new ReportPlaceForecastMonth(forecastFacade
                .findByPlaceIdAndYearAndBeforeMonth(placeId, year, month), balance);
        }
        return new ReportPlaceForecastMonth(forecastFacade
            .findByPlaceIdAndYearAndMonth(placeId, year, month), balance);
    }

    @Override
    @RolesAllowed(MANAGER)
    public boolean isReportForYear(Year year, Long placeId) {
        return reportFacade.findReportYearsByPlaceId(placeId).stream()
            .anyMatch((e) -> e.getValue() == year.getValue());
    }

    @Override
    @RolesAllowed(OWNER)
    public boolean isOwnReportForYear(Year year, Long placeId, String login) throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        checkUserPlace(place, login);
        return reportFacade.findReportYearsByPlaceId(placeId).stream()
            .anyMatch((e) -> e.getValue() == year.getValue());
    }

    @RolesAllowed({OWNER, MANAGER})
    private void checkUserPlace(Place place, String login) throws AppBaseException {
        if (place.getOwners().stream().noneMatch((o) -> o.getAccount().getLogin().equals(login))) {
            throw new InaccessibleReportException();
        }
    }

    @PermitAll
    @Override
    public void createReportForPlace(Long placeId) throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        List<Category> categories = categoryFacade.findAll();
        Year year = Year.of(LocalDateTime.now().getYear() - 1);
        for (Category cat : categories) {
            List<Cost> costs = costFacade.findByYearAndCategoryId(year, cat.getId());

            LocalDate januaryFirst = LocalDate.now().withDayOfYear(1);
            var rate = rateFacade.findFirstInYear(januaryFirst, cat.getId());

            BigDecimal costForPlace;
            BigDecimal consumptionForPlace;

            BigDecimal totalConsumption = costs.stream()
                .map(Cost::getTotalConsumption)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (rate.getAccountingRule() == AccountingRule.UNIT) {
                costForPlace = rate.getValue();
                consumptionForPlace = BigDecimal.ONE;
            } else {
                BigDecimal totalCost = costs.stream()
                    .map(c -> c.getRealRate().multiply(c.getTotalConsumption()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal multiplier;
                if (rate.getAccountingRule() == AccountingRule.PERSON) {
                    consumptionForPlace = BigDecimal.valueOf(place.getResidentsNumber());
                    multiplier = consumptionForPlace.divide(totalConsumption, 6, RoundingMode.CEILING);
                } else if (rate.getAccountingRule() == AccountingRule.SURFACE) {
                    consumptionForPlace = place.getSquareFootage();
                    multiplier = consumptionForPlace.divide(totalConsumption, 6, RoundingMode.CEILING);
                } else { // METER
                    List<Reading> readings = readingFacade.findReliableReadingsFromLastDayOfYear(
                        placeId, cat.getId(), LocalDate.now().getYear());

                    if (readings.size() == 2) {
                        consumptionForPlace = readings.get(0).getValue().subtract(readings.get(1).getValue())
                            .divide(totalConsumption, 6, RoundingMode.CEILING);
                        multiplier = consumptionForPlace.divide(totalConsumption, 6, RoundingMode.CEILING);
                    } else if (readings.size() == 1) {
                        consumptionForPlace = readings.get(0).getValue();
                        multiplier = consumptionForPlace.divide(totalConsumption, 6, RoundingMode.CEILING);
                    } else {
                        consumptionForPlace = BigDecimal.ZERO;
                        multiplier = BigDecimal.ZERO;
                    }
                }

                costForPlace = totalCost.multiply(multiplier);
            }

            Report report = new Report(year, costForPlace, consumptionForPlace, place, cat);
            reportFacade.create(report);
        }
    }
}
