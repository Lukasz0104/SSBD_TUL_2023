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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppRollbackLimitExceededException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.RepeatedPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.AppOptimisticLockException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.ForcePasswordChangeDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.LanguageChangeDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.OverrideForcedPasswordDatabaseException;
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

    @Context
    private SecurityContext securityContext;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register/owner")
    public Response registerOwner(@Valid RegisterOwnerDto registerOwnerDto) throws AppBaseException {
        Account account = createAccountFromRegisterDto(registerOwnerDto);
        Address address = createAddressFromDto(registerOwnerDto.getAddress());
        AccessLevel accessLevel = new OwnerData(account, address);

        account.getAccessLevels().add(accessLevel);

        accountManager.registerAccount(account);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register/manager")
    public Response registerManager(@Valid RegisterManagerDto registerManagerDto) throws AppBaseException {
        Account account = createAccountFromRegisterDto(registerManagerDto);
        Address address = createAddressFromDto(registerManagerDto.getAddress());

        AccessLevel accessLevel = new ManagerData(account, address, registerManagerDto.getLicenseNumber());

        account.getAccessLevels().add(accessLevel);

        accountManager.registerAccount(account);
        return Response.noContent().build();
    }

    @POST
    @Path("/confirm-registration")
    public Response confirmRegistration(@NotNull @QueryParam("token") UUID token) throws AppBaseException {
        accountManager.confirmRegistration(token);
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

        if (dto.getOldPassword().equals(dto.getNewPassword())) {
            throw new RepeatedPasswordException();
        }

        try {
            String login = securityContext.getUserPrincipal().getName();
            accountManager.changePassword(dto.getOldPassword(), dto.getNewPassword(), login);
        } catch (AppDatabaseException appDatabaseException) {
            // TODO: repeat transaction
        }

        return Response.noContent().build();
    }

    @POST
    @Path("/change-email")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response changeEmail() throws AppBaseException {

        accountManager.changeEmail(securityContext.getUserPrincipal().getName());
        return Response.noContent().build();
    }

    @PUT
    @Path("/confirm-email")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response confirmEmail(@Valid ChangeEmailDto dto, @NotNull @QueryParam("token") UUID token)
        throws AppBaseException {

        accountManager.confirmEmail(dto.getEmail(), token, securityContext.getUserPrincipal().getName());
        return Response.ok().build();
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
        OwnAccountDto dto = createOwnAccountDto(accountManager.getAccountDetails(login));
        String ifMatch = jwsProvider.signPayload(dto.getSignableFields());
        return Response.ok().entity(dto).tag(ifMatch).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response getAccountDetails(@PathParam("id") Long id) throws AppBaseException {
        AccountDto dto = createAccountDto(accountManager.getAccountDetails(id));
        String ifMatch = jwsProvider.signPayload(dto.getSignableFields());
        return Response.ok().entity(dto).tag(ifMatch).build();
    }

    @PUT
    @Path("/me/change-access-level")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public AccessTypeDto changeAccessLevel(@Valid ChangeAccessLevelDto accessLevelDto) throws AppBaseException {
        AccessType accessType;
        try {
            accessType = AccessType.valueOf(accessLevelDto.getAccessType());
        } catch (IllegalArgumentException e) {
            throw new InvalidAccessLevelException();
        }

        accessType = accountManager.changeAccessLevel(securityContext.getUserPrincipal().getName(), accessType);

        return new AccessTypeDto(accessType);
    }

    @PUT
    @Path("/change-language/{language}")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response changeLanguage(@NotBlank @PathParam("language") String language) throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        int txCounter = 0;
        do {
            try {
                accountManager.changeAccountLanguage(securityContext.getUserPrincipal().getName(),
                    language.toUpperCase());
                return Response.status(Response.Status.NO_CONTENT).build();
            } catch (AppDatabaseException ade) {
                txCounter++;
            }
        } while (txCounter < txLimit);
        throw new LanguageChangeDatabaseException();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response getAllAccounts(@DefaultValue("true") @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getAllAccounts(active));
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/owners")
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Response getOwnerAccounts(@DefaultValue("true") @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getOwnerAccounts(active));
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/managers")
    @RolesAllowed({"ADMIN"})
    public Response getManagerAccounts(@DefaultValue("true") @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getManagerAccounts(active));
        return Response.ok(accounts).build();
    }

    @GET
    @Path("/admins")
    @RolesAllowed({"ADMIN"})
    public Response getAdminAccounts(@DefaultValue("true") @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getAdminAccounts(active));
        return Response.ok(accounts).build();
    }

    @PUT
    @Path("/force-password-change/{login}")
    @RolesAllowed({"ADMIN"})
    public Response forcePasswordChange(@NotBlank @PathParam("login") String login) throws AppBaseException {
        if (login.equals(securityContext.getUserPrincipal().getName())) {
            throw new IllegalSelfActionException();
        }
        int txLimit = properties.getTransactionRepeatLimit();
        int txCounter = 0;
        do {
            try {
                accountManager.forcePasswordChange(login);
                return Response.noContent().build();
            } catch (AppDatabaseException ade) {
                txCounter++;
            }
        } while (txCounter < txLimit);
        throw new ForcePasswordChangeDatabaseException();
    }

    @PUT
    @Path("/override-forced-password")
    public Response overrideForcedPassword(@Valid @NotNull ResetPasswordDto resetPasswordDto)
        throws AppBaseException {
        int txLimit = properties.getTransactionRepeatLimit();
        int txCounter = 0;
        do {
            try {
                accountManager.overrideForcedPassword(resetPasswordDto.getPassword(),
                    UUID.fromString(resetPasswordDto.getToken()));
                return Response.noContent().build();
            } catch (AppDatabaseException ade) {
                txCounter++;
            }
        } while (txCounter < txLimit);
        throw new OverrideForcedPasswordDatabaseException();
    }

    @PUT
    @Path("/me")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public Response updatePersonalData(@Valid @NotNull EditOwnPersonalDataDto editOwnPersonalDataDTO)
        throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        OwnAccountDto ownAccountDto = AccountDtoConverter.createOwnAccountDto(
            accountManager.editPersonalData(createAccountFromEditOwnPersonalDataDto(editOwnPersonalDataDTO), login));
        return Response.ok().entity(ownAccountDto).build();
    }

    @PUT
    @Path("/admin/edit-other")
    @RolesAllowed({"ADMIN"})
    public Response editDetailsByAdmin(@Valid @NotNull EditAnotherPersonalDataDto dto) throws AppBaseException {
        Account account = createAccountFromEditDto(dto);
        String adminLogin = securityContext.getUserPrincipal().getName();
        AccountDto accountDto = createAccountDto(accountManager.editPersonalDataByAdmin(account, adminLogin));
        return Response.ok().entity(accountDto).build();
    }
}
