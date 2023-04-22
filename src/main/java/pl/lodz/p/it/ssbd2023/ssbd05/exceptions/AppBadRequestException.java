package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppBadRequestException extends AppBaseException {
    public AppBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
