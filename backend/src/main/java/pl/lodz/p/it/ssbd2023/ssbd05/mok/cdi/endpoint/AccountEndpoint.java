package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountFromEditDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountFromEditOwnPersonalDataDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountFromRegisterDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAddressFromDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createOwnAccountDto;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessType;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidCaptchaCodeException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.SignatureMismatchException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.forbidden.IllegalSelfActionException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.EditAnotherPersonalDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.EditOwnPersonalDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterManagerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ResetPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccessTypeDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.OwnAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.JwsProvider;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.Properties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.RecaptchaService;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.annotations.ValidUUID;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter;

import java.util.List;
import java.util.UUID;

@RequestScoped
@Path("/accounts")
public class AccountEndpoint {

    @Inject
    private Properties properties;

    @Inject
    private AccountManagerLocal accountManager;

    @Inject
    private JwsProvider jwsProvider;

    @Inject
    private RecaptchaService recaptchaService;

    @Context
    private SecurityContext securityContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register/owner")
    public Response registerOwner(@NotNull @Valid RegisterOwnerDto registerOwnerDto) throws AppBaseException {
        if (!recaptchaService.verifyCode(registerOwnerDto.getCaptchaCode())) {
            throw new InvalidCaptchaCodeException();
        }

        Account account = createAccountFromRegisterDto(registerOwnerDto);
        Address address = createAddressFromDto(registerOwnerDto.getAddress());
        AccessLevel accessLevel = new OwnerData(account, address);

        account.getAccessLevels().add(accessLevel);

        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollbackTX = false;

        do {
            accountManager.registerAccount(account);
            rollbackTX = accountManager.isLastTransactionRollback();
        } while (rollbackTX && --txLimit > 0);

        if (rollbackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register/manager")
    public Response registerManager(@NotNull @Valid RegisterManagerDto registerManagerDto) throws AppBaseException {
        if (!recaptchaService.verifyCode(registerManagerDto.getCaptchaCode())) {
            throw new InvalidCaptchaCodeException();
        }

        Account account = createAccountFromRegisterDto(registerManagerDto);
        Address address = createAddressFromDto(registerManagerDto.getAddress());

        AccessLevel accessLevel = new ManagerData(account, address, registerManagerDto.getLicenseNumber());

        account.getAccessLevels().add(accessLevel);

        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollbackTX = false;

        do {
            accountManager.registerAccount(account);
            rollbackTX = accountManager.isLastTransactionRollback();
        } while (rollbackTX && --txLimit > 0);

        if (rollbackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @POST
    @Path("/confirm-registration")
    public Response confirmRegistration(@ValidUUID @QueryParam("token") String token) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollbackTX = false;
        do {
            try {
                accountManager.confirmRegistration(UUID.fromString(token));
                rollbackTX = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollbackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }

        } while (rollbackTX && --txLimit > 0);

        if (rollbackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @POST
    @PermitAll
    @Path("/reset-password-message")
    public Response sendResetPasswordMessage(@NotBlank @Email @QueryParam("email") String email)
        throws AppBaseException {
        accountManager.sendResetPasswordMessage(email);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/reset-password")
    public Response resetPassword(@NotNull @Valid ResetPasswordDto resetPasswordDto) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        do {
            try {
                accountManager.resetPassword(resetPasswordDto.getPassword(),
                    UUID.fromString(resetPasswordDto.getToken()));
                rollBackTX = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollBackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @PUT
    @Path("/me/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response changePassword(@Valid @NotNull ChangePasswordDto dto) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        do {
            try {
                String login = securityContext.getUserPrincipal().getName();
                accountManager.changePassword(dto.getOldPassword(), dto.getNewPassword(), login);
                rollBackTX = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollBackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @POST
    @Path("/me/change-email")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response changeEmail() throws AppBaseException {

        accountManager.changeEmail(securityContext.getUserPrincipal().getName());
        return Response.noContent().build();
    }

    @PUT
    @Path("/me/confirm-email/{token}")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response confirmEmail(@NotNull @Valid ChangeEmailDto dto, @ValidUUID @PathParam("token") String token)
        throws AppBaseException {

        int retryTXCounter = properties.getTransactionRepeatLimit();
        boolean rollbackTX = false;
        do {
            try {
                accountManager.confirmEmail(dto.getEmail(), UUID.fromString(token),
                    securityContext.getUserPrincipal().getName());
                rollbackTX = accountManager.isLastTransactionRollback();

            } catch (AppOptimisticLockException aole) {
                rollbackTX = true;
                if (retryTXCounter < 2) {
                    throw aole;
                }
            }
        } while (rollbackTX && --retryTXCounter > 0);

        if (rollbackTX && retryTXCounter == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/manager/change-active-status")
    @RolesAllowed({"MANAGER"})
    public Response changeActiveStatusAsManager(@NotNull @Valid ChangeActiveStatusDto dto)
        throws AppBaseException {
        String managerLogin = securityContext.getUserPrincipal().getName();

        int retryTXCounter = properties.getTransactionRepeatLimit();
        boolean rollbackTX = false;
        do {
            try {
                accountManager.changeActiveStatusAsManager(managerLogin,
                    dto.getId(), dto.getActive());
                rollbackTX = accountManager.isLastTransactionRollback();

            } catch (AppOptimisticLockException aole) {
                rollbackTX = true;
                if (retryTXCounter < 2) {
                    throw aole;
                }
            }
        } while (rollbackTX && --retryTXCounter > 0);

        if (rollbackTX && retryTXCounter == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/admin/change-active-status")
    @RolesAllowed({"ADMIN"})
    public Response changeActiveStatusAsAdmin(@NotNull @Valid ChangeActiveStatusDto dto)
        throws AppBaseException {
        String adminLogin = securityContext.getUserPrincipal().getName();

        int retryTXCounter = properties.getTransactionRepeatLimit();
        boolean rollbackTX = false;
        do {
            try {
                accountManager.changeActiveStatusAsAdmin(adminLogin,
                    dto.getId(), dto.getActive());
                rollbackTX = accountManager.isLastTransactionRollback();

            } catch (AppOptimisticLockException aole) {
                rollbackTX = true;
                if (retryTXCounter < 2) {
                    throw aole;
                }
            }
        } while (rollbackTX && --retryTXCounter > 0);

        if (rollbackTX && retryTXCounter == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response getOwnAccountDetails() throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();

        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        OwnAccountDto dto;
        do {
            dto = createOwnAccountDto(accountManager.getAccountDetails(login));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        String ifMatch = jwsProvider.signPayload(dto.getSignableFields());
        return Response.ok().entity(dto).header("ETag", ifMatch).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response getAccountDetails(@PathParam("id") Long id) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        AccountDto dto;
        do {
            dto = createAccountDto(accountManager.getAccountDetails(id));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        String ifMatch = jwsProvider.signPayload(dto.getSignableFields());
        return Response.ok().entity(dto).header("ETag", ifMatch).build();
    }

    @PUT
    @Path("/me/change-access-level")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response changeAccessLevel(@Valid @NotNull ChangeAccessLevelDto accessLevelDto) throws AppBaseException {
        AccessType accessType = AccessType.valueOf(accessLevelDto.getAccessType());

        accessType = accountManager.changeAccessLevel(securityContext.getUserPrincipal().getName(), accessType);

        return Response.ok().entity(new AccessTypeDto(accessType)).build();
    }

    @PUT
    @Path("/me/change-language/{language}")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response changeLanguage(@NotBlank @PathParam("language") String language) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        do {
            try {
                accountManager.changeAccountLanguage(securityContext.getUserPrincipal().getName(),
                    language.toUpperCase());
                rollBackTX = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollBackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @GET
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Response getAllAccounts(@DefaultValue("true") @QueryParam("active") Boolean active) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<AccountDto> accounts;
        do {
            accounts = AccountDtoConverter.createAccountDtoList(accountManager.getAllAccounts(active));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/owners")
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Response getOwnerAccounts(@DefaultValue("true") @QueryParam("active") Boolean active)
        throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<AccountDto> accounts;
        do {
            accounts = AccountDtoConverter.createAccountDtoList(accountManager.getOwnerAccounts(active));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/owners/unapproved")
    @RolesAllowed({"MANAGER"})
    public Response getUnapprovedOwnerAccounts() throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<AccountDto> accounts;
        do {
            accounts = AccountDtoConverter.createAccountDtoList(accountManager.getUnapprovedOwnerAccounts());
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/managers")
    @RolesAllowed({"ADMIN"})
    public Response getManagerAccounts(@DefaultValue("true") @QueryParam("active") Boolean active)
        throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<AccountDto> accounts;
        do {
            accounts = AccountDtoConverter.createAccountDtoList(accountManager.getManagerAccounts(active));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/managers/unapproved")
    @RolesAllowed({"ADMIN"})
    public Response getUnapprovedManagerAccounts() throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<AccountDto> accounts;
        do {
            accounts = AccountDtoConverter.createAccountDtoList(accountManager.getUnapprovedManagerAccounts());
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/admins")
    @RolesAllowed({"ADMIN"})
    public Response getAdminAccounts(@DefaultValue("true") @QueryParam("active") Boolean active)
        throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;

        List<AccountDto> accounts;
        do {
            accounts = AccountDtoConverter.createAccountDtoList(accountManager.getAdminAccounts(active));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }
        return Response.ok(accounts).build();
    }

    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/force-password-change/{login}")
    public Response forcePasswordChange(@NotBlank @PathParam("login") String login) throws AppBaseException {
        if (login.equals(securityContext.getUserPrincipal().getName())) {
            throw new IllegalSelfActionException();
        }
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        do {
            try {
                accountManager.forcePasswordChange(login);
                rollBackTX = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollBackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @PUT
    @PermitAll
    @Path("/override-forced-password")
    public Response overrideForcedPassword(@Valid @NotNull ResetPasswordDto resetPasswordDto)
        throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        do {
            try {
                accountManager.overrideForcedPassword(resetPasswordDto.getPassword(),
                    UUID.fromString(resetPasswordDto.getToken()));
                rollBackTX = accountManager.isLastTransactionRollback();
            } catch (AppOptimisticLockException aole) {
                rollBackTX = true;
                if (txLimit < 2) {
                    throw aole;
                }
            }
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.noContent().build();
    }

    @PUT
    @Path("/me")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response editPersonalData(
        @Valid @NotNull EditOwnPersonalDataDto editOwnPersonalDataDTO,
        @NotNull @HeaderParam("If-Match") String ifMatch)
        throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();

        if (!jwsProvider.verify(ifMatch, editOwnPersonalDataDTO.getSignableFields())) {
            throw new SignatureMismatchException();
        }

        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        OwnAccountDto ownAccountDto = null;
        do {
            ownAccountDto = AccountDtoConverter.createOwnAccountDto(
                accountManager.editPersonalData(createAccountFromEditOwnPersonalDataDto(editOwnPersonalDataDTO),
                    login));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.ok().entity(ownAccountDto).build();
    }

    @PUT
    @Path("/admin/edit-other")
    @RolesAllowed({"ADMIN"})
    public Response editPersonalDataByAdmin(
        @Valid @NotNull EditAnotherPersonalDataDto dto,
        @NotNull @HeaderParam("If-Match") String ifMatch) throws AppBaseException {

        if (!jwsProvider.verify(ifMatch, dto.getSignableFields())) {
            throw new SignatureMismatchException();
        }

        if (dto.getLogin().equals(securityContext.getUserPrincipal().getName())) {
            throw new IllegalSelfActionException();
        }

        Account account = createAccountFromEditDto(dto);
        AccountDto accountDto = null;

        int txLimit = properties.getTransactionRepeatLimit();
        boolean rollBackTX = false;
        do {
            accountDto = createAccountDto(accountManager.editPersonalDataByAdmin(account));
            rollBackTX = accountManager.isLastTransactionRollback();
        } while (rollBackTX && --txLimit > 0);

        if (rollBackTX && txLimit == 0) {
            throw new AppRollbackLimitExceededException();
        }

        return Response.ok().entity(accountDto).build();
    }
}
