package pl.lodz.p.it.ssbd2023.ssbd05.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.PostalCode;

public record AddressDto(
    @PostalCode
    String postalCode,

    @NotNull
    @NotBlank
    @Size(min = 2, max = 85)
    @Pattern(regexp = "[A-ZĄĆĘŁÓŚŹŻ]+.*")
    String city,

    @NotNull
    @NotBlank
    @Size(max = 85)
    String street,

    @Positive
    @NotNull
    Integer buildingNumber
) {
}
