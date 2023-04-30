package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountFromRegisterDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAddressFromDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createOwnAccountDto;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
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
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.DatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.InvalidAccessLevelException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.LanguageChangeDatabaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.conflict.RepeatedPasswordException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterManagerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ResetPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccessTypeDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.OwnAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.Properties;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter;

import java.util.List;
import java.util.UUID;

@RequestScoped
@Path("/accounts")
public class AccountEndpoint {

    @Inject
    private AccountManagerLocal accountManager;

    @Inject
    Properties properties;

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
    @Path("/reset-password-message")
    public Response sendResetPasswordMessage(@NotNull @Email @QueryParam("email") String email)
        throws AppBaseException {
        accountManager.sendResetPasswordMessage(email);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/reset-password")
    public Response resetPassword(@Valid ResetPasswordDto resetPasswordDto) throws AppBaseException {
        try {
            accountManager.resetPassword(resetPasswordDto.getPassword(), resetPasswordDto.getToken());
        } catch (DatabaseException e) {
            // TODO
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
        } catch (DatabaseException databaseException) {
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
    public Response changeActiveStatusAsManager(@Valid ChangeActiveStatusDto dto)
        throws AppBaseException {
        String managerLogin = securityContext.getUserPrincipal().getName();

        accountManager.changeActiveStatusAsManager(managerLogin,
            dto.getId(), dto.getActive());
        return Response.noContent().build();
    }

    @PUT
    @Path("/admin/change-active-status")
    @RolesAllowed({"ADMIN"})
    public Response changeActiveStatusAsAdmin(@Valid ChangeActiveStatusDto dto)
        throws AppBaseException {
        String adminLogin = securityContext.getUserPrincipal().getName();

        accountManager.changeActiveStatusAsAdmin(adminLogin,
            dto.getId(), dto.getActive());
        return Response.noContent().build();
    }

    @GET
    @Path("/me/details")
    @RolesAllowed({"ADMIN", "MANAGER", "OWNER"})
    public OwnAccountDto getOwnAccountDetails() throws AppBaseException {
        String login = securityContext.getUserPrincipal().getName();
        return createOwnAccountDto(accountManager.getAccountDetails(login));
    }

    @GET
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public AccountDto getAccountDetails(@PathParam("id") Long id) throws AppBaseException {
        return createAccountDto(accountManager.getAccountDetails(id));
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
            } catch (DatabaseException e) {
                txCounter++;
            }
        } while (txCounter < txLimit);
        throw new LanguageChangeDatabaseException();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response getAllAccounts(@NotNull @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getAllAccounts(active));
        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    @GET
    @Path("owners")
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Response getOwnerAccounts(@NotNull @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getOwnerAccounts(active));
        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    @GET
    @Path("managers")
    @RolesAllowed({"ADMIN"})
    public Response getManagerAccounts(@NotNull @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getManagerAccounts(active));
        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    @GET
    @Path("admins")
    @RolesAllowed({"ADMIN"})
    public Response getAdminAccounts(@NotNull @QueryParam("active") Boolean active) {
        List<AccountDto> accounts = AccountDtoConverter.createAccountDtoList(accountManager.getAdminAccounts(active));
        return Response.status(Response.Status.OK).entity(accounts).build();
    }
}
