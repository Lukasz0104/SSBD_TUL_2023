package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppForbiddenException extends AppBaseException {

    public AppForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
