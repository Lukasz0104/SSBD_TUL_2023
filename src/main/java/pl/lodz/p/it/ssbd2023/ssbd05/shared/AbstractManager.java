package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import static jakarta.ejb.TransactionAttributeType.NOT_SUPPORTED;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TransactionAttribute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractManager {

    @Resource
    SessionContext sctx;

    protected static final Logger LOGGER = Logger.getGlobal();

    private String transactionId;

    private boolean lastTransactionRollback;

    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isLastTransactionRollback() {
        return lastTransactionRollback;
    }

    public void afterBegin() {
        transactionId = Long.toString(System.currentTimeMillis())
                        + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        LOGGER.log(
            Level.INFO,
            "Transakcja TXid={0} rozpoczęta w {1}, tożsamość: {2}",
            new Object[] {
                transactionId,
                this.getClass().getName(),
                sctx.getCallerPrincipal().getName()});
    }

    public void beforeCompletion() {
        LOGGER.log(
            Level.INFO,
            "Transakcja TXid={0} przed zatwierdzeniem w {1} tożsamość: {2}",
            new Object[] {
                transactionId,
                this.getClass().getName(),
                sctx.getCallerPrincipal().getName()});
    }

    public void afterCompletion(boolean committed) {
        lastTransactionRollback = !committed;
        LOGGER.log(
            Level.INFO,
            "Transakcja TXid={0} zakończona w {1} poprzez {3}, tożsamość {2}",
            new Object[] {
                transactionId,
                this.getClass().getName(),
                sctx.getCallerPrincipal().getName(),
                committed ? "ZATWIERDZENIE" : "ODWOŁANIE"});
    }

}
