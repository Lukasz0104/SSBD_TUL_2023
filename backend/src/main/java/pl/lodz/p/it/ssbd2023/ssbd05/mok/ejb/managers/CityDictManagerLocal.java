package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;

import java.util.List;

public interface CityDictManagerLocal extends CommonManagerInterface {

    List<String> getCitiesStartingWith(String pattern) throws AppBaseException;

}