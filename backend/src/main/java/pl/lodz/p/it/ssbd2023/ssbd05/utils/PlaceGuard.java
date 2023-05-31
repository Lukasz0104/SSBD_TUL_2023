package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.Place;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppForbiddenException;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.PlaceFacade;

@ApplicationScoped
public class PlaceGuard {

    @Inject
    private PlaceFacade placeFacade;

    @Context
    private SecurityContext securityContext;

    public void checkAccessToPlace(Long id) throws AppBaseException {
        Place place = placeFacade.find(id)
            .orElseThrow(AppForbiddenException::new);

    }

}
