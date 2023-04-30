package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ActivityTracker;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AdminData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.ActivityTrackerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AdminDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.ManagerDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.OwnAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.OwnerDataDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountDtoConverter {
    public static Account createAccountFromRegisterDto(RegisterAccountDto dto) {
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

    public static AddressDto createAddressDtoFromAdress(Address address) {
        return new AddressDto(
            address.getPostalCode(),
            address.getCity(),
            address.getStreet(),
            address.getBuildingNumber()
        );
    }

    public static OwnAccountDto createOwnAccountDto(Account account) {
        return createAccountDto(account);
    }

    public static AccountDto createAccountDto(Account account) {
        return new AccountDto(
            account.getId(),
            account.getVersion(),
            createAccessLevelDtoSet(account.getAccessLevels()),
            account.getEmail(),
            account.getLogin(),
            account.getFirstName(),
            account.getLastName(),
            account.getLanguage().toString(),
            account.isVerified(),
            account.isActive(),
            createActivityTrackerDto(account.getActivityTracker())
        );
    }

    public static ActivityTrackerDto createActivityTrackerDto(ActivityTracker activityTracker) {
        return new ActivityTrackerDto(
            activityTracker.getLastSuccessfulLogin(),
            activityTracker.getLastUnsuccessfulLogin(),
            activityTracker.getLastSuccessfulLoginIp(),
            activityTracker.getLastUnsuccessfulLoginIp(),
            activityTracker.getUnsuccessfulLoginChainCounter()
        );
    }

    public static Set<AccessLevelDto> createAccessLevelDtoSet(Set<AccessLevel> accessLevels) {
        Set<AccessLevelDto> accessLevelDtoSet = new HashSet<>();
        for (AccessLevel accessLevel : accessLevels) {
            if (!accessLevel.isActive()) {
                continue;
            }
            if (accessLevel instanceof OwnerData ownerData) {
                accessLevelDtoSet.add(
                    new OwnerDataDto(
                        ownerData.getId(),
                        ownerData.getVersion(),
                        ownerData.getLevel(),
                        createAddressDtoFromAdress(ownerData.getAddress())
                    )
                );
            } else if (accessLevel instanceof ManagerData managerData) {
                accessLevelDtoSet.add(
                    new ManagerDataDto(
                        managerData.getId(),
                        managerData.getVersion(),
                        managerData.getLevel(),
                        createAddressDtoFromAdress(managerData.getAddress()),
                        managerData.getLicenseNumber()
                    )
                );
            } else if (accessLevel instanceof AdminData adminData) {
                accessLevelDtoSet.add(
                    new AdminDataDto(
                        adminData.getId(),
                        adminData.getVersion(),
                        adminData.getLevel()
                    )
                );
            }
        }
        return accessLevelDtoSet;
    }

    public static List<AccountDto> createAccountDtoList(List<Account> accounts) {
        return accounts.stream()
            .map(AccountDtoConverter::createAccountDto)
            .toList();
    }
}
