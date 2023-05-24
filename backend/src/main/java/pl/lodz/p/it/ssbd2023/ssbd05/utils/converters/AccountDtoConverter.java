package pl.lodz.p.it.ssbd2023.ssbd05.utils.converters;

import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ActivityTracker;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AdminData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Language;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AdminDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.ManagerDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.OwnerDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddManagerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddOwnerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.EditAnotherPersonalDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.EditOwnPersonalDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.ActivityTrackerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.OwnAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountDtoConverter {

    public static Account createAccountFromRegisterDto(RegisterAccountDto dto) {
        Language lang;

        try {
            lang = Language.valueOf(dto.getLanguage().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            lang = Language.EN;
        }

        return new Account(
            dto.getEmail(),
            dto.getPassword(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getLogin(),
            lang);
    }

    public static Account createAccountFromEditOwnPersonalDataDto(EditOwnPersonalDataDto dto) {
        return new Account(
            dto.getVersion(),
            createAccessLevelSet(dto.getAccessLevels()),
            dto.getFirstName(),
            dto.getLastName()
        );
    }

    public static Address createAddressFromDto(AddressDto dto) {
        return new Address(dto.postalCode(), dto.city(), dto.street(), dto.buildingNumber());
    }

    public static Account createAccountFromEditDto(EditAnotherPersonalDataDto dto) {
        return new Account(
            dto.getLogin(),
            dto.getVersion(),
            dto.getEmail(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getLanguage(),
            createAccessLevelSet(dto.getAccessLevels())
        );
    }

    public static Set<AccessLevel> createAccessLevelSet(Collection<AccessLevelDto> accessLevelDtos) {
        Set<AccessLevel> accessLevelSet = new HashSet<>();
        for (AccessLevelDto accessLevelDto : accessLevelDtos) {
            if (accessLevelDto instanceof OwnerDataDto ownerDataDto) {
                accessLevelSet.add(new OwnerData(ownerDataDto.getId(), ownerDataDto.getVersion(),
                    createAddressFromDto(ownerDataDto.getAddress()), true));
            } else if (accessLevelDto instanceof ManagerDataDto managerDataDto) {
                accessLevelSet.add(
                    new ManagerData(managerDataDto.getId(), managerDataDto.getVersion(),
                        createAddressFromDto(managerDataDto.getAddress()), true,
                        managerDataDto.getLicenseNumber()));
            } else if (accessLevelDto instanceof AdminDataDto adminDataDto) {
                accessLevelSet.add(new AdminData(adminDataDto.getId(), adminDataDto.getVersion(), true));
            }
        }
        return accessLevelSet;
    }


    public static AddressDto createAddressDtoFromAddress(Address address) {
        return new AddressDto(
            address.getPostalCode(),
            address.getCity(),
            address.getStreet(),
            address.getBuildingNumber()
        );
    }

    public static OwnAccountDto createOwnAccountDto(Account account) {
        return new OwnAccountDto(
            account.getId(),
            account.getVersion(),
            createAccessLevelDtoSet(account.getAccessLevels()),
            account.getEmail(),
            account.getLogin(),
            account.getFirstName(),
            account.getLastName(),
            account.getLanguage().toString(),
            account.isTwoFactorAuth(),
            account.getCreatedTime(),
            (account.getCreatedBy() != null) ? account.getCreatedBy().getLogin() : "anonymous",
            account.getUpdatedTime(),
            (account.getUpdatedBy() != null) ? account.getUpdatedBy().getLogin() : "anonymous"
        );
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
            account.isTwoFactorAuth(),
            account.getCreatedTime(),
            (account.getCreatedBy() != null) ? account.getCreatedBy().getLogin() : "anonymous",
            account.getUpdatedTime(),
            (account.getUpdatedBy() != null) ? account.getUpdatedBy().getLogin() : "anonymous",
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
            if (accessLevel instanceof OwnerData ownerData) {
                accessLevelDtoSet.add(
                    new OwnerDataDto(
                        ownerData.getId(),
                        ownerData.getVersion(),
                        ownerData.isVerified(),
                        ownerData.isActive(),
                        ownerData.getCreatedTime(),
                        (ownerData.getCreatedBy() != null) ? ownerData.getCreatedBy().getLogin() : "anonymous",
                        ownerData.getUpdatedTime(),
                        (ownerData.getUpdatedBy() != null) ? ownerData.getUpdatedBy().getLogin() : "anonymous",
                        createAddressDtoFromAddress(ownerData.getAddress())
                    )
                );
            } else if (accessLevel instanceof ManagerData managerData) {
                accessLevelDtoSet.add(
                    new ManagerDataDto(
                        managerData.getId(),
                        managerData.getVersion(),
                        managerData.isVerified(),
                        managerData.isActive(),
                        managerData.getCreatedTime(),
                        (managerData.getCreatedBy() != null) ? managerData.getCreatedBy().getLogin() : "anonymous",
                        managerData.getUpdatedTime(),
                        (managerData.getUpdatedBy() != null) ? managerData.getUpdatedBy().getLogin() : "anonymous",
                        createAddressDtoFromAddress(managerData.getAddress()),
                        managerData.getLicenseNumber()
                    )
                );
            } else if (accessLevel instanceof AdminData adminData) {
                accessLevelDtoSet.add(
                    new AdminDataDto(
                        adminData.getId(),
                        adminData.getVersion(),
                        adminData.isVerified(),
                        adminData.isActive(),
                        adminData.getCreatedTime(),
                        (adminData.getCreatedBy() != null) ? adminData.getCreatedBy().getLogin() : "anonymous",
                        adminData.getUpdatedTime(),
                        (adminData.getUpdatedBy() != null) ? adminData.getUpdatedBy().getLogin() : "anonymous"
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

    public static AccessLevel createManagerAccessLevelFromDto(AddManagerAccessLevelDto dto) {
        return new ManagerData(createAddressFromDto(dto.address()), dto.licenseNumber());
    }

    public static AccessLevel createOwnerAccessLevelFromDto(AddOwnerAccessLevelDto dto) {
        return new OwnerData((createAddressFromDto(dto.address())));
    }

    public static Page<AccountDto> createAccountDtoPage(Page<Account> accountPage) {
        List<AccountDto> list = accountPage.getData().stream()
            .map(AccountDtoConverter::createAccountDto)
            .toList();

        return new Page<>(list, accountPage.getTotalSize(), accountPage.getPageSize(),
            accountPage.getCurrentPage());
    }
}
