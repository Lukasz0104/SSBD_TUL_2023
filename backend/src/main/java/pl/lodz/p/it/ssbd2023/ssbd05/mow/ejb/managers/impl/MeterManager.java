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
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Reading;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.notfound.MeterNotFoundException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.GenericManagerExceptionsInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.MeterFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.ReadingFacade;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.managers.MeterManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.AbstractManager;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

@Stateful
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors({
    GenericManagerExceptionsInterceptor.class,
    LoggerInterceptor.class,
})
@DenyAll
public class MeterManager extends AbstractManager implements MeterManagerLocal, SessionSynchronization {

    @Inject
    private MeterFacade meterFacade;

    @Inject
    private ReadingFacade readingFacade;

    @Override
    @RolesAllowed(MANAGER)
    public Page<Reading> getMeterReadingsAsManager(Long id, int page, int pageSize) throws AppBaseException {
        if (!meterFacade.existsById(id)) {
            throw new MeterNotFoundException();
        }
        return readingFacade.findByMeterId(id, page, pageSize);
    }

    @Override
    @RolesAllowed(OWNER)
    public Page<Reading> getMeterReadingsAsOwner(Long id, String login, int page, int pageSize)
        throws AppBaseException {
        if (!meterFacade.existsByIdAndOwnerLogin(id, login)) {
            throw new MeterNotFoundException();
        }
        return readingFacade.findByMeterId(id, page, pageSize);
    }
}
