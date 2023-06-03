package pl.lodz.p.it.ssbd2023.ssbd05.utils.rollback;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

@FunctionalInterface
public interface FunctionReturnRWithThrows<R> {

    R apply() throws AppBaseException;
}