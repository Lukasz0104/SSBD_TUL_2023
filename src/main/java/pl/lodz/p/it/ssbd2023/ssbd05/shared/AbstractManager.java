package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import static jakarta.ejb.TransactionAttributeType.NOT_SUPPORTED;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.SessionSynchronization;
import jakarta.ejb.TransactionAttribute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class AbstractManager implements SessionSynchronization {

    @Resource
    SessionContext sctx;

    protected static final Logger LOGGER = Logger.getGlobal();

    private String transactionId;

    private boolean lastTransactionRollback;

    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isLastTransactionRollback() {
        return lastTransactionRollback;
    }

    @Override
    public void afterBegin() {
        transactionId = Long.toString(System.currentTimeMillis())
            + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
        LOGGER.log(Level.INFO, "Transaction TXid={0} has begun at {1}, "
            + " caller: {2}", new Object[] {transactionId,
            this.getClass().getName(), sctx.getCallerPrincipal().getName()});
    }

    @Override
    public void beforeCompletion() {
        LOGGER.log(Level.INFO, "Transaction TXid={0} before completion at"
            + " {1} caller: {2}", new Object[] {transactionId,
            this.getClass().getName(), sctx.getCallerPrincipal().getName()});
    }

    @Override
    public void afterCompletion(boolean committed) {
        lastTransactionRollback = !committed;
        LOGGER.log(
            Level.INFO,
            "Transaction TXid={0} has completed at {1} by {3}, caller {2}",
            new Object[] {
                transactionId,
                this.getClass().getName(), sctx.getCallerPrincipal().getName(),
                committed ? "COMMIT" : "ROLLBACK"});
    }
}
