package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import jakarta.ejb.Local;

@Local
public interface CommonManagerInterface {
    boolean isLastTransactionRollback();
}
