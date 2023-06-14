package pl.lodz.p.it.ssbd2023.ssbd05.utils.functionalinterfaces;

import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;

@FunctionalInterface
public interface FunctionThrowsVoidTwoArgs<A1, A2> {
    void apply(A1 arg1, A2 arg2) throws AppBaseException;
}
