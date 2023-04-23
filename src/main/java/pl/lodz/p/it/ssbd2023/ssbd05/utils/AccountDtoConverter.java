package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterAccountDto;

public class AccountDtoConverter {
    public static Account createAccountFromDto(RegisterAccountDto dto) {
        return new Account(
            dto.getEmail(),
            dto.getPassword(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getLogin());
    }

    public static Address createAddressFromDto(AddressDto dto) {
        return new Address(dto.postalCode(), dto.city(), dto.street(), dto.buildingNumber());
    }
}
