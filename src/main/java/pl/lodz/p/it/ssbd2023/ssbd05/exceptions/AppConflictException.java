package pl.lodz.p.it.ssbd2023.ssbd05.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AppConflictException extends AppBaseException {
    public AppConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
