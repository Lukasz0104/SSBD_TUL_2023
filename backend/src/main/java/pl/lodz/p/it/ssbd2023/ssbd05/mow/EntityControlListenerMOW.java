package pl.lodz.p.it.ssbd2023.ssbd05.mow;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.AbstractEntity;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.ejb.facades.AccountFacade;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

public class EntityControlListenerMOW {
    @PrePersist
    void prePersist(AbstractEntity abstractEntity) {
        String username = CDI.current().select(Principal.class).get().getName();
        AccountFacade accountFacade = CDI.current().select(AccountFacade.class).get();

        Optional<Account> account = accountFacade.findByLogin(username);
        account.ifPresentOrElse(abstractEntity::setCreatedBy, () -> abstractEntity.setCreatedBy(null));
        abstractEntity.setCreatedTime(LocalDateTime.now());
    }

    @PreUpdate
    void preUpdate(AbstractEntity abstractEntity) {
        String username = CDI.current().select(Principal.class).get().getName();
        AccountFacade accountFacade = CDI.current().select(AccountFacade.class).get();

        Optional<Account> account = accountFacade.findByLogin(username);
        account.ifPresent((account1) -> {
            abstractEntity.setUpdatedBy(account1);
            abstractEntity.setUpdatedTime(LocalDateTime.now());
        });
    }
}
