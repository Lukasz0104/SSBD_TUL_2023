package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.MANAGER;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Building;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericFacadeExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;
import java.util.Optional;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@DenyAll
@Interceptors({
    GenericFacadeExceptionsInterceptor.class,
    LoggerInterceptor.class
})
public class BuildingFacade extends AbstractFacade<Building> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public BuildingFacade() {
        super(Building.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    @RolesAllowed(MANAGER)
    public Optional<Building> find(Long id) {
        return super.find(id);
    }

    @Override
    @RolesAllowed(MANAGER)
    public void edit(Building building) throws AppBaseException {
        super.edit(building);
    }

    @PermitAll
    public Building findByAddress(Address address) {
        TypedQuery<Building> tq = em.createNamedQuery("Building.findByAddress", Building.class);
        tq.setParameter("address", address);
        return tq.getSingleResult();
    }

    @PermitAll
    public List<Building> findByBuildingNumber(Integer buildingNumber) {
        TypedQuery<Building> tq = em.createNamedQuery("Building.findByBuildingNumber", Building.class);
        tq.setParameter("buildingNumber", buildingNumber);
        return tq.getResultList();
    }

    @Override
    @RolesAllowed({OWNER, MANAGER, ADMIN})
    public List<Building> findAll() {
        return super.findAll();
    }

    @RolesAllowed(MANAGER)
    @Override
    public Optional<Building> find(Long id) {
        return super.find(id);
    }
}
