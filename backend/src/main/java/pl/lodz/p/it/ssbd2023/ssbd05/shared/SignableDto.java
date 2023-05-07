package pl.lodz.p.it.ssbd2023.ssbd05.shared;

import jakarta.json.bind.annotation.JsonbTransient;

public interface SignableDto {
    @JsonbTransient
    String getSignableFields();
}
