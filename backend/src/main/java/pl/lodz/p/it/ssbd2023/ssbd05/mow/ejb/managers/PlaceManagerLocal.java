package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.util.List;
import java.util.Set;

@Local
public interface PlaceManagerLocal extends CommonManagerInterface {
    List<Place> getAllPlaces() throws AppBaseException;

    List<Place> getOwnPlaces(String login) throws AppBaseException;

    Place getPlaceDetailsAsOwner(Long id, String login) throws AppBaseException;

    Place getPlaceDetailsAsManager(Long id) throws AppBaseException;

    List<Rate> getPlaceRates(Long id) throws AppBaseException;

    List<Report> getPlaceReports(Long id) throws AppBaseException;

    Set<Meter> getPlaceMetersAsManager(Long id) throws AppBaseException;

    Set<Meter> getPlaceMetersAsOwner(Long id, String login) throws AppBaseException;

    void createPlace() throws AppBaseException;

    List<OwnerData> getPlaceOwners(Long id) throws AppBaseException;

    void addOwnerToPlace(Long id) throws AppBaseException;

    void removeOwnerFromPlace(Long id) throws AppBaseException;

    List<Rate> getCurrentRatesFromPlace(Long id) throws AppBaseException;

    void addCategoryToPlace(Long id) throws AppBaseException;

    void removeCategoryFromPlace(Long id) throws AppBaseException;

    void editPlaceDetails(Long id) throws AppBaseException;
}
