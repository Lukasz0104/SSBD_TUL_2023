package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppUnauthorizedException extends AppBaseException {
    public AppUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}