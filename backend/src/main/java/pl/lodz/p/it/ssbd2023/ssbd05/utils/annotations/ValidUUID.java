package pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.UUID;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@UUID(message = I18n.INVALID_UUID)
@Constraint(validatedBy = {})
public @interface ValidUUID {
    String message() default I18n.INVALID_UUID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


