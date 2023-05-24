package pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.ejb.Stateful;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.facades.CityDictFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;

import java.util.List;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class CityDictManager extends AbstractManager implements CityDictManagerLocal {

    @Inject
    private CityDictFacade cityDictFacade;

    @Override
    @PermitAll
    public List<String> getCitiesStartingWith(String pattern) {
        return cityDictFacade.findCitiesStartingWithAndLimitTo10(pattern);
    }

}