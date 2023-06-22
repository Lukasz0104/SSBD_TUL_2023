package pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull
@Pattern(regexp = "^\\d{2}-\\d{3}$", message = I18n.INVALID_POSTAL_CODE)
@Documented
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostalCode {
    String message() default I18n.INVALID_POSTAL_CODE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
