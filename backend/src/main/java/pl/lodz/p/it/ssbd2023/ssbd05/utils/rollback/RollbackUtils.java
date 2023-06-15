package pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppTransactionRolledBackException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.CommonManagerInterface;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.AppProperties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.functionalinterfaces.FunctionNoReturnWithThrows;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.functionalinterfaces.FunctionReturnRWithThrows;

@ApplicationScoped
@Interceptors(LoggerInterceptor.class)
public class RollbackUtils {

    @Inject
    private AppProperties appProperties;

    public <T, M extends CommonManagerInterface> Response.ResponseBuilder
        rollBackTXBasicWithOkStatus(FunctionReturnRWithThrows<T> func, M manager) throws AppBaseException {
        T dto = this.rollBackTXBasicWithReturnTypeT(func, manager);
        return Response.ok(dto);
    }

    public <M extends  CommonManagerInterface> Response.ResponseBuilder
        rollBackTXWithOptimisticLockReturnNoContentStatus(FunctionNoReturnWithThrows func, M manager)
        throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX;

        do {
            try {
                func.apply();
                rollBackTX = manager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollBackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent();
    }

    public <M extends  CommonManagerInterface> Response.ResponseBuilder
        rollBackTXBasicWithReturnNoContentStatus(FunctionNoReturnWithThrows func, M manager) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX;

        do {
            try {
                func.apply();
                rollBackTX = manager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent();
    }

    public <T, M extends  CommonManagerInterface> T
        rollBackTXBasicWithReturnTypeT(FunctionReturnRWithThrows<T> func, M manager) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollBackTX;

        T dto = null;
        do {
            try {
                dto = func.apply();
                rollBackTX = manager.isLastTransactionRollback();
            } catch (AppTransactionRolledBackException atrbe) {
                rollBackTX = true;
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return dto;
    }


    public <T, M extends  CommonManagerInterface> T
        rollBackTXWithOptimisticLockReturnTypeT(FunctionReturnRWithThrows<T> func, M manager) throws AppBaseException {
        int txLimit = appProperties.getTransactionRepeatLimit();
        boolean rollbackTX;
        T dto = null;
        do {
            try {
                dto = func.apply();
                rollbackTX = manager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollbackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            } catch (AppTransactionRolledBackException atrbe) {
                rollbackTX = true;
            }
        } while (rollbackTX && --txLimit > 0);

        if (rollbackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return dto;
    }


}
