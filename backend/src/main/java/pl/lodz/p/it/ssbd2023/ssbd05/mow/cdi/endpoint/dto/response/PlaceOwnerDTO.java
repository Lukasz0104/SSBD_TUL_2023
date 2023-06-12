package pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response;

import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;

public record PlaceOwnerDTO(
    Long ownerDataId,
    String firstName,
    String lastName,
    AddressDto address,
    boolean active
) {
}
