package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

@FunctionalInterface
public interface FunctionThrows<R> {

    R apply() throws AppBaseException;

}
