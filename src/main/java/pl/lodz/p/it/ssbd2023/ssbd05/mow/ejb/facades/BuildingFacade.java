package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Building;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractFacade;

import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
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

    public Building findByAddress(Address address) {
        TypedQuery<Building> tq = em.createNamedQuery("Building.findByAddress", Building.class);
        tq.setParameter("address", address);
        return tq.getSingleResult();
    }

    public List<Building> findByBuildingNumber(Integer buildingNumber) {
        TypedQuery<Building> tq = em.createNamedQuery("Building.findByBuildingNumber", Building.class);
        tq.setParameter("buildingNumber", buildingNumber);
        return tq.getResultList();
    }

}
