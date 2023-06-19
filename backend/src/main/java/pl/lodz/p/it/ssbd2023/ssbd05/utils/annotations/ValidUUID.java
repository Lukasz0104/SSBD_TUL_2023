package pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}",
    message = I18n.INVALID_UUID)
@Constraint(validatedBy = {})
public @interface ValidUUID {
    String message() default I18n.INVALID_UUID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


