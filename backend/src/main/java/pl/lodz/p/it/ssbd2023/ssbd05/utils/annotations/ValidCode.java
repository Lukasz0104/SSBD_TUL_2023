package pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^[0-9]{8}$", message = I18n.INVALID_CODE)
public @interface ValidCode {
    String message() default I18n.INVALID_CODE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
