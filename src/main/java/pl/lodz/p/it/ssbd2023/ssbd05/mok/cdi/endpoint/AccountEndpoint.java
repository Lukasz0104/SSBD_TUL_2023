package pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint;

import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAccountFromDto;
import static pl.lodz.p.it.ssbd2023.ssbd05.utils.converters.AccountDtoConverter.createAddressFromDto;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.Address;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.ManagerData;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.OwnerData;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.badrequest.PasswordNotMatchingException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterManagerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RegisterOwnerDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ResetPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManagerLocal;

import java.util.UUID;

@RequestScoped
@Path("/accounts")
public class AccountEndpoint {

    @Inject
    private AccountManagerLocal accountManager;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register/owner")
    public Response registerOwner(@Valid RegisterOwnerDto registerOwnerDto) throws AppBaseException {
        Account account = createAccountFromDto(registerOwnerDto);
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
        Account account = createAccountFromDto(registerManagerDto);
        Address address = createAddressFromDto(registerManagerDto.getAddress());

        AccessLevel accessLevel = new ManagerData(account, address, registerManagerDto.getLicenseNumber());

        account.getAccessLevels().add(accessLevel);

        accountManager.registerAccount(account);
        return Response.noContent().build();
    }

    @POST
    @Path("/confirm-registration")
    public Response confirmRegistration(@NotNull @QueryParam("token") UUID token)
        throws AppBaseException {
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
        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())) {
            throw new PasswordNotMatchingException();
        }
        accountManager.resetPassword(resetPasswordDto.getPassword(), resetPasswordDto.getToken());
        return Response.noContent().build();
    }
}
