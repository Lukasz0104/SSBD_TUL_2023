package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.CategoryInUseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.InactivePlaceException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.InitialReadingRequiredException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.IllegalSelfActionException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.MeterNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.PlaceNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.RateNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.MeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.RateFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.PlaceManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class PlaceManager extends AbstractManager implements PlaceManagerLocal, SessionSynchronization {

    @Inject
    private PlaceFacade placeFacade;

    @Inject
    private MeterFacade meterFacade;

    @Inject
    private RateFacade rateFacade;

    @Override
    @RolesAllowed(MANAGER)
    public List<Place> getAllPlaces() throws AppBaseException {
        return placeFacade.findAll();
    }

    @Override
    @RolesAllowed(OWNER)
    public List<Place> getOwnPlaces(String login) throws AppBaseException {
        return placeFacade.findByLogin(login);
    }

    @Override
    @RolesAllowed({OWNER})
    public Place getPlaceDetailsAsOwner(Long id, String login) throws AppBaseException {
        return placeFacade.findByIdAndOwnerLogin(id, login).orElseThrow(PlaceNotFoundException::new);
    }

    @Override
    @RolesAllowed({MANAGER})
    public Place getPlaceDetailsAsManager(Long id) throws AppBaseException {
        return placeFacade.find(id).orElseThrow(PlaceNotFoundException::new);
    }

    @Override
    @RolesAllowed(OWNER)
    public List<Rate> getPlaceRates(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({OWNER, MANAGER})
    public List<Report> getPlaceReports(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER})
    public Set<Meter> getPlaceMetersAsManager(Long id) throws AppBaseException {
        Place place = placeFacade.find(id).orElseThrow(PlaceNotFoundException::new);
        return place.getMeters();
    }

    @Override
    @RolesAllowed({OWNER})
    public Set<Meter> getPlaceMetersAsOwner(Long id, String login) throws AppBaseException {
        Place place = placeFacade.findByIdAndOwnerLogin(id, login).orElseThrow(PlaceNotFoundException::new);
        return place.getMeters();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void createPlace() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public List<OwnerData> getPlaceOwners(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void addOwnerToPlace(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void removeOwnerFromPlace(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER})
    public List<Rate> getCurrentRatesFromPlace(Long id) throws AppBaseException {
        return placeFacade.findCurrentRateByPlaceId(id);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void addCategoryToPlace(Long placeId, Long categoryId, BigDecimal value, String login)
        throws AppBaseException {
        Place place = placeFacade.find(placeId).orElseThrow(PlaceNotFoundException::new);
        if (place.getOwners().stream().anyMatch((owner) -> owner.getAccount().getLogin().equals(login))) {
            throw new IllegalSelfActionException();
        }
        if (place.getCurrentRates().stream().anyMatch((p) -> p.getCategory().getId().equals(categoryId))) {
            throw new CategoryInUseException();
        }
        if (!place.isActive()) {
            throw new InactivePlaceException();
        }
        Rate rate = rateFacade.findCurrentRateByCategoryId(categoryId).orElseThrow(RateNotFoundException::new);

        if (rate.getAccountingRule().equals(AccountingRule.METER)) {
            Meter meter = null;
            try {
                meter = meterFacade.findByCategoryIdAndPlaceId(categoryId, placeId)
                    .orElseThrow(MeterNotFoundException::new);
                if (meter.getReadings().size() < 1 && value == null) {
                    throw new InitialReadingRequiredException();
                } else {
                    meter.getReadings().add(new Reading(LocalDateTime.now(), value, meter));
                }
            } catch (MeterNotFoundException mnfe) {
                if (value == null) {
                    throw new InitialReadingRequiredException();
                } else {
                    meter = new Meter(rate.getCategory(), place);
                    meter.getReadings().add(new Reading(LocalDateTime.now(), value, meter));
                }
            }
            place.getMeters().add(meter);
        }
        place.getCurrentRates().add(rate);
        placeFacade.edit(place);
    }

    @Override
    @RolesAllowed(MANAGER)
    public boolean checkIfCategoryRequiresReading(Long placeId, Long categoryId) throws AppBaseException {
        Rate rate = rateFacade.findCurrentRateByCategoryId(categoryId).orElseThrow(RateNotFoundException::new);
        if (!rate.getAccountingRule().equals(AccountingRule.METER)) {
            return false;
        }
        try {
            Meter meter = meterFacade.findByCategoryIdAndPlaceId(categoryId, placeId)
                .orElseThrow(MeterNotFoundException::new);
            return meter.getReadings().size() < 1;
        } catch (MeterNotFoundException mnfe) {
            return true;
        }
    }

    @Override
    @RolesAllowed(MANAGER)
    public void removeCategoryFromPlace(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void editPlaceDetails(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
