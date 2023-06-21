package pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@NotNull
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^\\d{6,8}$",
    message = I18n.INVALID_LICENSE_NUMBER)
@Constraint(validatedBy = {})
public @interface ValidLicenseNumber {
    String message() default I18n.INVALID_UUID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
