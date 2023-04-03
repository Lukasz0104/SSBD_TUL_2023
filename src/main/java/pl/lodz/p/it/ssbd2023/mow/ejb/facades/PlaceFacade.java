package pl.lodz.p.it.ssbd2023.mow.ejb.facades;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import pl.lodz.p.it.ssbd2023.entities.Address;
import pl.lodz.p.it.ssbd2023.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.shared.AbstractFacade;

import java.math.BigDecimal;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class PlaceFacade extends AbstractFacade<Place> {

    @PersistenceContext(unitName = "ssbd05mowPU")
    private EntityManager em;

    public PlaceFacade() {
        super(Place.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Place findByAddress(Address address) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findByAddress", Place.class);
        tq.setParameter("address", address);
        return tq.getSingleResult();
    }

    public List<Place> findByPlaceNumber(Integer placeNumber) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findByPlaceNumber", Place.class);
        tq.setParameter("placeNumber", placeNumber);
        return tq.getResultList();
    }

    public List<Place> findByResidentsNumber(Integer residentsNumber) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findByResidentsNumber", Place.class);
        tq.setParameter("residentsNumber", residentsNumber);
        return tq.getResultList();
    }


    public List<Place> findBySquareFootage(BigDecimal squareFootage) {
        TypedQuery<Place> tq = em.createNamedQuery("Place.findByResidentsNumber", Place.class);
        tq.setParameter("squareFootage", squareFootage);
        return tq.getResultList();
    }

    public List<Place> findByActive(boolean active) {
        TypedQuery<Place> tq;
        if (active) {
            tq = em.createNamedQuery("Place.findAllActive", Place.class);
        } else {
            tq = em.createNamedQuery("Place.findAllInactive", Place.class);
        }
        return tq.getResultList();
    }

    public List<Place> findByResidentsNumberAndActive(Integer residentsNumber, boolean active) {
        TypedQuery<Place> tq;
        if (active) {
            tq = em.createNamedQuery("Place.findByResidentsNumberAndActive", Place.class);
            tq.setParameter("residentsNumber", residentsNumber);
        } else {
            tq = em.createNamedQuery("Place.findByResidentsNumberAndInactive", Place.class);
            tq.setParameter("residentsNumber", residentsNumber);
        }
        return tq.getResultList();
    }

    public List<Place> findBySquareFootageAndActive(Integer squareFootage, boolean active) {
        TypedQuery<Place> tq;
        if (active) {
            tq = em.createNamedQuery("Place.findBySquareFootageAndActive", Place.class);
            tq.setParameter("squareFootage", squareFootage);
        } else {
            tq = em.createNamedQuery("Place.findBySquareFootageAndInactive", Place.class);
            tq.setParameter("squareFootage", squareFootage);
        }
        return tq.getResultList();
    }

    public List<Place> findByAddressAndActive(Address address, boolean active) {
        TypedQuery<Place> tq;
        if (active) {
            tq = em.createNamedQuery("Place.findByAddressAndActive", Place.class);
            tq.setParameter("address", address);
        } else {
            tq = em.createNamedQuery("Place.findByAddressAndInactive", Place.class);
            tq.setParameter("address", address);
        }
        return tq.getResultList();
    }
}
