package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.impl;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Building;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.BuildingFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.BuildingManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.util.List;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class BuildingManager extends AbstractManager implements BuildingManagerLocal, SessionSynchronization {

    @Inject
    private BuildingFacade buildingFacade;

    @Override
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public List<Building> getAllBuildings() throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({ADMIN, MANAGER, OWNER})
    public List<Report> getBuildingReports(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public List<Place> getBuildingPlaces(Long id) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed({MANAGER, OWNER})
    public Report getBuildingReportByYear(Long id, Long year) throws AppBaseException {
        throw new UnsupportedOperationException();
    }

    @Override
    @RolesAllowed(MANAGER)
    public void createBuilding() throws AppBaseException {
        throw new UnsupportedOperationException();
    }
}
