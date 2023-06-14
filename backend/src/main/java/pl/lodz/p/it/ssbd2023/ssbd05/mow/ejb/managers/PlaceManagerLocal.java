package pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers;

import jakarta.ejb.Local;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Meter;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Rate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Report;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.math.BigDecimal;
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

    void createPlace(Integer placeNumber, BigDecimal squareFootage, Integer residentsNumber, Long buildingNumber)
        throws AppBaseException;

    List<OwnerData> getPlaceOwners(Long id) throws AppBaseException;

    void addOwnerToPlace(Long id, String login) throws AppBaseException;

    List<Account> getOwnerNotOwningPlace(Long id) throws AppBaseException;

    void removeOwnerFromPlace(Long id, String login) throws AppBaseException;

    List<Rate> getCurrentRatesFromPlace(Long id) throws AppBaseException;

    List<Rate> getCurrentRatesFromOwnPlace(Long id, String login) throws AppBaseException;

    void addCategoryToPlace(Long placeId, Long categoryId, BigDecimal value, String login) throws AppBaseException;

    void removeCategoriesFromPlace(Long placeId, Long categoryId, String login) throws AppBaseException;

    boolean checkIfCategoryRequiresReading(Long placeId, Long categoryId) throws AppBaseException;

    void editPlaceDetails(Long id, Place newPlace) throws AppBaseException;

    List<Rate> findCurrentRateByPlaceIdNotMatch(Long id) throws AppBaseException;

}
