package pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotBlank
@Size(min = 8)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*?[0-9])(?=.*?[!_$@?()\\[\\]#%])[A-Za-z0-9!#%]{8,32}$")
public @interface ValidPassword {
}


