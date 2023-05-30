import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.ADMIN;
import static pl.lodz.p.it.ssbd2023.ssbd05.shared.Roles.OWNER;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Language;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AdminDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.ManagerDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.OwnerDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddManagerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddOwnerAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.EditAnotherPersonalDataDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RefreshJwtDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ResetPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.OwnAccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/*
    W celu uruchomienia testów należy dodać pliki certyfikatu, z podowdów bezpieczeństwa nie zostały one umieszczone w
    repozytorium. Pliki potrzebne do uruchomienia to:
        - fullchain.pem
        - privkey.pem
        Należy je umięcić w katalogu: /backend/src/test/resources/data/gw/letsencrypt/live/team-5.proj-sum.it.p.lodz.pl/
    Oraz plik:
        - ssl-dhparams.pem
        Który należy umieścić w katalogu: /backend/src/test/resources/data/gw/letsencrypt/
 */
public class MokITests extends TestContainersSetup {

    // Zaloguj się
    @Nested
    class MOK1 {
        @Test
        void shouldLoginWhenCredentialsAreValidAndAccountHasActiveAndVerifiedAccessLevels() {
            LoginDto loginDto = new LoginDto("pduda", "P@ssw0rd");

            String refreshToken = given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class))
                .extract()
                .jsonPath()
                .getString("refreshToken");

            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("pduda", refreshToken);

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class));
        }

        @Test
        void shouldReturnSC401WhenAccountDoesntExist() {
            LoginDto loginDto = new LoginDto("randomAccount", "randomPassword");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenPasswordIsInvalid() {
            LoginDto loginDto = new LoginDto("dchmielewski", "wrongPassword");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenAccountIsLocked() {
            LoginDto loginDto = new LoginDto("aczarnecki", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenAccountIsUnverified() {
            LoginDto loginDto = new LoginDto("ntracz", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenAccountHasNoActiveAccessLevels() {
            LoginDto loginDto = new LoginDto("bkowalewski", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenLoginIsEmpty() {
            LoginDto loginDto = new LoginDto("", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC401WhenPasswordIsEmpty() {
            LoginDto loginDto = new LoginDto("pduda", "");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldLockAccountAfterThreeUnsuccessfulLoginAttemptsInARow() {
            LoginDto loginDto = new LoginDto("jstanczyk", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class));

            LoginDto wrongLoginDto = new LoginDto("jstanczyk", "wrongPassword");

            for (int i = 0; i < 3; i++) {
                given()
                    .contentType(ContentType.JSON)
                    .body(wrongLoginDto)
                    .when()
                    .post("/login")
                    .then()
                    .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                    .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
            }

            given().spec(adminSpec)
                .when()
                .get("/accounts/-14")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(false));

            given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenRefreshTokenWasUsed() {
            LoginDto loginDto = new LoginDto("pduda", "P@ssw0rd");

            String refreshToken = given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class))
                .extract()
                .jsonPath()
                .getString("refreshToken");

            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("pduda", refreshToken);

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.allOf(Matchers.any(String.class), Matchers.not(refreshToken)),
                    "language", Matchers.any(String.class));

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC401WhenGivenLoginDoesntMatchRefreshTokenLogin() {
            LoginDto loginDto = new LoginDto("pduda", "P@ssw0rd");

            String refreshToken = given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class))
                .extract()
                .jsonPath()
                .getString("refreshToken");

            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("bjaworski", refreshToken);

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldReturnSC400WhenRefreshTokenIsNotUUID() {
            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("pduda", "definitelyNotUUID");

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenRefreshTokenIsEmpty() {
            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("pduda", "");

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenLoginIsEmpty() {
            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("", UUID.randomUUID().toString());

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC401WhenRefreshTokenDoesntExist() {
            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("pduda", UUID.randomUUID().toString());

            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .assertThat()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldRefreshOnlyOneSessionForConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            LoginDto loginDto = new LoginDto("ikaminski", "P@ssw0rd");

            String refreshToken = given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class))
                .extract()
                .jsonPath()
                .getString("refreshToken");

            int threadNumber = 5;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    RefreshJwtDto refreshJwtDto = new RefreshJwtDto("ikaminski", refreshToken);

                    int statusCode = given().body(refreshJwtDto)
                        .contentType(ContentType.JSON)
                        .when()
                        .post("/refresh").getStatusCode();

                    if (statusCode == 200) {
                        numberOfSuccessfulAttempts.getAndIncrement();
                    }
                    numberFinished.getAndIncrement();
                }));
            }
            threads.forEach(Thread::start);
            cyclicBarrier.await();
            while (numberFinished.get() != threadNumber) {
            }

            assertEquals(1, numberOfSuccessfulAttempts.get());
        }
    }

    // Wyloguj się
    @Nested
    class MOK3 {
        @Test
        void shouldCreateTokenAndSendPasswordResetMessageToUser() {
            given()
                .queryParam("email", "ikaminski@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(adminSpec)
                .queryParam("email", "ikaminski@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .queryParam("email", "ikaminski@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(managerSpec)
                .queryParam("email", "ikaminski@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldReturnSC204WhenAccountDoesntExist() {
            given()
                .queryParam("email", "non_existent@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenUnverifiedAccount() {

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("accounts/-9")
                .then()
                .assertThat()
                .body("verified", Matchers.equalTo(false));

            given()
                .queryParam("email", "rnowak@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.UNVERIFIED_ACCOUNT));
        }

        @Test
        void shouldReturnSC403WhenInactiveAccount() {

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("accounts/-10")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(false));

            given()
                .queryParam("email", "nkowalska@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.INACTIVE_ACCOUNT));
        }

        @Test
        void shouldReturnSC400WhenWrongRequestParameters() {

            // Empty email
            given()
                .queryParam("email", "")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // Provided value is not an email
            given()
                .queryParam("email", "email")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // No parameter provided
            given()
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);
        }

        @Test
        void shouldReturnSC400WhenTokenNotFoundDuringPasswordResetConfirm() {
            ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("message", Matchers.equalTo(I18n.TOKEN_NOT_FOUND));
        }

        @Test
        void shouldReturnSC400WhenWrongParametersDuringPasswordResetConfirm() {
            // Invalid password, should have at least one number and one special character
            ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
            resetPasswordDto.setPassword("newPassword");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // Invalid password, should have at least one number and one special character
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("not_uuid");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // Empty password
            resetPasswordDto.setPassword("");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // Empty token
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);
        }
    }

    // Wyświetl dane konta użytkownika
    @Nested
    class MOK4 {

        private static final String ownProfileURL = "/accounts/me";
        private static final String profileURL = "/accounts/%d";
        private static RequestSpecification testSpec;

        @BeforeAll
        static void generateTestSpec() {
            LoginDto loginDto = new LoginDto("kgraczyk", "P@ssw0rd");

            String jwt = RestAssured.given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");

            testSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();
        }

        @Test
        void shouldPassGettingOwnAccountDetails() {
            io.restassured.response.Response response = given().spec(testSpec)
                .when()
                .get(ownProfileURL)
                .thenReturn();

            assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());

            String etag = response.getHeader("ETag");
            assertNotNull(etag);
            assertTrue(etag.length() > 0);

            OwnAccountDto dto = response.getBody().as(OwnAccountDto.class);
            assertNotNull(dto);
            assertEquals("kgraczyk", dto.getLogin());
            assertEquals("kgraczyk@gmail.local", dto.getEmail());
            assertEquals("PL", dto.getLanguage());
            assertEquals("Kamil", dto.getFirstName());
            assertEquals("Graczyk", dto.getLastName());
            assertEquals(-27, dto.getId());
            assertEquals(2, dto.getAccessLevels().size());
            for (AccessLevelDto level : dto.getAccessLevels()) {
                if (level instanceof OwnerDataDto ownerData) {
                    assertTrue(ownerData.isActive());
                    assertTrue(ownerData.isVerified());

                    AddressDto addressDto = ownerData.getAddress();
                    assertEquals(14, addressDto.buildingNumber());
                    assertEquals("Łódź", addressDto.city());
                    assertEquals("99-150", addressDto.postalCode());
                    assertEquals("Smutna", addressDto.street());
                } else if (level instanceof ManagerDataDto managerData) {
                    assertEquals("9566541", managerData.getLicenseNumber());
                    assertTrue(managerData.isActive());
                    assertTrue(managerData.isVerified());

                    AddressDto addressDto = managerData.getAddress();
                    assertEquals(14, addressDto.buildingNumber());
                    assertEquals("Łódź", addressDto.city());
                    assertEquals("99-150", addressDto.postalCode());
                    assertEquals("Smutna", addressDto.street());
                }
            }
        }

        @Test
        void shouldReturnSC403WhenGettingOwnAccountDetailsAsGuest() {
            when()
                .get(ownProfileURL)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldPassGettingAccountDetails() {
            io.restassured.response.Response response = given().spec(adminSpec)
                .when()
                .get(profileURL.formatted(-27))
                .thenReturn();

            assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());

            String etag = response.getHeader("ETag");
            assertNotNull(etag);
            assertTrue(etag.length() > 0);

            AccountDto dto = response.getBody().as(AccountDto.class);
            assertNotNull(dto);
            assertEquals("kgraczyk", dto.getLogin());
            assertEquals("kgraczyk@gmail.local", dto.getEmail());
            assertEquals("PL", dto.getLanguage());
            assertEquals("Kamil", dto.getFirstName());
            assertEquals("Graczyk", dto.getLastName());
            assertEquals(-27, dto.getId());
            assertEquals(2, dto.getAccessLevels().size());

            assertTrue(dto.isActive());
            assertTrue(dto.isVerified());
            for (AccessLevelDto level : dto.getAccessLevels()) {
                if (level instanceof OwnerDataDto ownerData) {
                    assertTrue(ownerData.isActive());
                    assertTrue(ownerData.isVerified());

                    AddressDto addressDto = ownerData.getAddress();
                    assertEquals(14, addressDto.buildingNumber());
                    assertEquals("Łódź", addressDto.city());
                    assertEquals("99-150", addressDto.postalCode());
                    assertEquals("Smutna", addressDto.street());
                } else if (level instanceof ManagerDataDto managerData) {
                    assertEquals("9566541", managerData.getLicenseNumber());
                    assertTrue(managerData.isActive());
                    assertTrue(managerData.isVerified());

                    AddressDto addressDto = managerData.getAddress();
                    assertEquals(14, addressDto.buildingNumber());
                    assertEquals("Łódź", addressDto.city());
                    assertEquals("99-150", addressDto.postalCode());
                    assertEquals("Smutna", addressDto.street());
                }
            }
        }

        @Test
        void shouldReturnSC403WhenGettingAccountDetailsAsManager() {
            given().spec(managerSpec)
                .when()
                .get(profileURL.formatted(-1))
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAccountDetailsAsOwnerWithStatusCode403() {
            given().spec(ownerSpec)
                .when()
                .get(profileURL.formatted(-1))
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC404WhenGettingNonExistentAccountDetails() {
            given().spec(adminSpec)
                .when()
                .get(profileURL.formatted(-2137))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    // Zmień własne hasło
    @Nested
    class MOK5 {

        @Test
        void shouldChangePasswordWhenProvidedValidNewPasswordAndValidOldPassword() {

            String oldPass = "P@ssw0rd";
            String newPass = "Ha0slo123@rd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals((Integer) account.get("version"), (Integer) oldAccount.get("version") + 1);
        }

        @Test
        public void shouldReturnSC401WhenProvidedWrongOldPassword() {
            String oldPass = "P@ssw0rd7";
            String newPass = "Haslo123@rd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedInvalidOldPassword() {
            String oldPass = "";
            String newPass = "Haslo123@rd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedWeakNewPasswordLength() {
            String oldPass = "P@ssw0rd";
            String newPass = "h@Sl0";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedWeakNewPasswordNumber() {
            String oldPass = "P@ssw0rd";
            String newPass = "h@Slopasswd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedWeakNewPasswordSpecialCharacter() {
            String oldPass = "P@ssw0rd";
            String newPass = "h4Slopa55wo";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedWeakNewPasswordLowerCase() {
            String oldPass = "P@ssw0rd";
            String newPass = "H@S7OPASSWD";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedWeakNewPasswordUpperCase() {
            String oldPass = "P@ssw0rd";
            String newPass = "h@slo1passwd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedIdenticalPasswords() {
            String oldPass = "P@ssw0rd";
            String newPass = "P@ssw0rd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC400WhenProvidedEmptyOldPasswords() {
            String oldPass = "";
            String newPass = "P@ssw0rd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().spec(adminSpec).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Test
        public void shouldReturnSC403WhenNoAccountIsLoggedIn() {
            String oldPass = "P@ssw0rd";
            String newPass = "h@P4ssw0rd";
            ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

            JsonPath oldAccount = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();

            given().when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals(account.get("version"), (Integer) oldAccount.get("version"));
        }

        @Nested
        class ConcurrentCases {

            @Test
            public void shouldOneRequestChangePassword() throws BrokenBarrierException, InterruptedException {
                OwnAccountDto oldAcc = given().spec(ownerSpec)
                    .when()
                    .get("/accounts/me").as(OwnAccountDto.class);

                String oldPass = "P@ssw0rd";
                String newPass = "Haslo123@rd" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                ChangePasswordDto dto = new ChangePasswordDto(oldPass, newPass);

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given().spec(ownerSpec).when()
                            .body(dto)
                            .contentType(ContentType.JSON)
                            .put("/accounts/me/change-password")
                            .getStatusCode();

                        if (statusCode == 204) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                assertEquals(1, numberOfSuccessfulAttempts.get());

                OwnAccountDto newAcc = given()
                    .spec(ownerSpec)
                    .when()
                    .get("/accounts/me")
                    .as(OwnAccountDto.class);
            }
        }
    }


    // Zmień adres e-mail przypisany do własnego konta
    @Nested
    class MOK6 {

        @Test
        void shouldReturnSC204AfterRequestEmailChangeWhenLoggedIn() {

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .post("/accounts/me/change-email")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(managerSpec)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/me/change-email")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/me/change-email")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldReturnSC403AfterRequestEmailChangeWhenNotLoggedIn() {
            given()
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts/me/change-email")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        // ------------------------------------------------------------
        @Test
        void shouldReturnSC403AfterConfirmEmailWhenNotLoggedIn() {
            ChangeEmailDto changeEmailDto = new ChangeEmailDto("test@gmail.local");
            UUID randomUUID = UUID.randomUUID();
            given()
                .contentType(ContentType.JSON)
                .body(changeEmailDto)
                .when()
                .put("/accounts/me/confirm-email/%s".formatted(randomUUID))
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC404AfterConfirmEmailWhenNoToken() {
            ChangeEmailDto changeEmailDto = new ChangeEmailDto("email@email.com");
            given()
                .contentType(ContentType.JSON)
                .spec(ownerSpec)
                .body(changeEmailDto)
                .when()
                .put("/accounts/me/confirm-email")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenConfirmEmailWithNotValidToken() {
            ChangeEmailDto changeEmailDto = new ChangeEmailDto("email@email.com");

            given()
                .contentType(ContentType.JSON)
                .spec(ownerSpec)
                .body(changeEmailDto)
                .when()
                .put("/accounts/me/confirm-email/%s".formatted("notValidToken"))
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenConfirmEmailWithEmptyBody() {
            UUID randomUUID = UUID.randomUUID();

            given()
                .contentType(ContentType.JSON)
                .spec(ownerSpec)
                .body("")
                .when()
                .put("/accounts/me/confirm-email/%s".formatted(randomUUID))
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        }

        @Test
        void shouldReturnSC400WhenConfirmEmailWithNotValidBody() {
            UUID randomUUID = UUID.randomUUID();
            ChangeEmailDto changeEmailDto = new ChangeEmailDto("notValidBody");
            given()
                .contentType(ContentType.JSON)
                .spec(ownerSpec)
                .body(changeEmailDto)
                .when()
                .put("/accounts/me/confirm-email/%s".formatted(randomUUID))
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

        }

        @Test
        void shouldReturnSC400WhenConfirmEmailWithNotFoundToken() {
            String validEmail = "email@email.com";
            ChangeEmailDto changeEmailDto = new ChangeEmailDto(validEmail);
            UUID randomUUID = UUID.randomUUID();

            given()
                .contentType(ContentType.JSON)
                .spec(ownerSpec)
                .body(changeEmailDto)
                .when()
                .put("/accounts/me/confirm-email/%s".formatted(randomUUID))
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat()
                .body("message", Matchers.equalTo(I18n.TOKEN_NOT_FOUND));
        }

    }

    // Zmień własne dane osobowe
    @Nested
    class MOK7 {
        private static RequestSpecification testSpec;
        private static final String ownProfileURL = "/accounts/me";

        @BeforeAll
        static void generateTestSpec() {
            LoginDto loginDto = new LoginDto("wodwazny", "P@ssw0rd");

            String jwt = RestAssured.given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");

            testSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();
        }

        @Test
        void shouldPassWhenEditingOwnAccountDetails() {
            io.restassured.response.Response response = given().spec(testSpec)
                .when()
                .get(ownProfileURL)
                .thenReturn();
            OwnAccountDto dto = response.body().as(OwnAccountDto.class);
            String etag = response.getHeader("ETag");

            dto.setFirstName("Wojroll");
            dto.setLastName("Brave");

            AddressDto addressDto = new AddressDto(
                "99-151",
                "Łęczyca",
                "Belwederska",
                12);
            String licenseNumber = "123456";
            for (AccessLevelDto level : dto.getAccessLevels()) {
                if (level instanceof OwnerDataDto ownerData) {
                    ownerData.setAddress(addressDto);
                } else if (level instanceof ManagerDataDto managerData) {
                    managerData.setLicenseNumber(licenseNumber);
                    managerData.setAddress(addressDto);
                }
            }

            given()
                .header("If-Match", etag)
                .spec(testSpec)
                .when()
                .contentType(ContentType.JSON)
                .body(dto)
                .put(ownProfileURL)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(Response.Status.OK.getStatusCode());

            io.restassured.response.Response edited = given().spec(testSpec)
                .when()
                .get(ownProfileURL)
                .thenReturn();

            OwnAccountDto editedDto = edited.as(OwnAccountDto.class);
            assertEquals(dto.getFirstName(), editedDto.getFirstName());
            assertEquals(dto.getLastName(), editedDto.getLastName());

            ManagerDataDto editedManagerData = edited.body().jsonPath()
                .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class);
            assertEquals(editedManagerData.getAddress(), addressDto);
            assertEquals(editedManagerData.getLicenseNumber(), licenseNumber);

            OwnerDataDto editedOwnerData = edited.body().jsonPath()
                .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class);

            assertEquals(editedOwnerData.getAddress(), addressDto);
        }

        @Test
        void shouldReturnSC403WhenEditingOwnAccountDetailsAsGuest() {
            io.restassured.response.Response response = given().spec(testSpec)
                .when()
                .get(ownProfileURL)
                .thenReturn();
            OwnAccountDto dto = response.body().as(OwnAccountDto.class);
            String etag = response.getHeader("ETag");

            given()
                .header("If-Match", etag)
                .when()
                .contentType(ContentType.JSON)
                .body(dto)
                .put(ownProfileURL)
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Nested
        class InactiveEdit {

            private static RequestSpecification omitManagerSpec;
            private static RequestSpecification omitOwnerSpec;

            @BeforeAll
            static void generateTestSpec() {
                LoginDto loginDto = new LoginDto("ptomczyk", "P@ssw0rd");

                String jwt = RestAssured.given().body(loginDto)
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/login")
                    .jsonPath()
                    .get("jwt");

                omitManagerSpec = new RequestSpecBuilder()
                    .addHeader("Authorization", "Bearer " + jwt)
                    .build();

                loginDto = new LoginDto("kkowalski", "P@ssw0rd");

                jwt = RestAssured.given().body(loginDto)
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/login")
                    .jsonPath()
                    .get("jwt");

                omitOwnerSpec = new RequestSpecBuilder()
                    .addHeader("Authorization", "Bearer " + jwt)
                    .build();
            }

            @Test
            void shouldOmitInactiveManagerAccessLevelWhenEditingOwnAccountDetails() {
                io.restassured.response.Response response = given().spec(omitManagerSpec)
                    .when()
                    .get(ownProfileURL)
                    .then().extract().response();

                OwnAccountDto dto = response.body().as(OwnAccountDto.class);

                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof ManagerDataDto managerData) {
                        managerData.setLicenseNumber("654321");
                        AddressDto addressDto = new AddressDto(
                            "99-151",
                            "Łęczyca",
                            "Belwederska",
                            12);
                        managerData.setAddress(addressDto);
                    }
                }

                given()
                    .header("If-Match", etag)
                    .spec(omitManagerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.OK.getStatusCode());

                io.restassured.response.Response edited = given().spec(omitManagerSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();

                OwnAccountDto verifyDto = edited.as(OwnAccountDto.class);

                String firstName = response.body().jsonPath().getString("firstName");
                assertEquals(firstName, verifyDto.getFirstName());

                String lastName = response.body().jsonPath().getString("lastName");
                assertEquals(lastName, verifyDto.getLastName());

                ManagerDataDto managerData = response.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class);
                ManagerDataDto editedManagerData = edited.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class);
                assertEquals(editedManagerData.getAddress(), managerData.getAddress());
                assertEquals(editedManagerData.getLicenseNumber(), managerData.getLicenseNumber());
            }

            @Test
            void shouldOmitInactiveOwnerAccessLevelWhenEditingOwnAccountDetails() {
                io.restassured.response.Response response = given().spec(omitOwnerSpec)
                    .when()
                    .get(ownProfileURL)
                    .then().extract().response();

                OwnAccountDto dto = response.body().as(OwnAccountDto.class);

                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof OwnerDataDto ownerData) {
                        AddressDto addressDto = new AddressDto(
                            "99-151",
                            "Łęczyca",
                            "Belwederska",
                            12);
                        ownerData.setAddress(addressDto);
                    }
                }

                given()
                    .header("If-Match", etag)
                    .spec(omitOwnerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.OK.getStatusCode());

                io.restassured.response.Response edited = given().spec(omitOwnerSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();

                OwnAccountDto verifyDto = edited.as(OwnAccountDto.class);

                String firstName = response.body().jsonPath().getString("firstName");
                assertEquals(firstName, verifyDto.getFirstName());

                String lastName = response.body().jsonPath().getString("lastName");
                assertEquals(lastName, verifyDto.getLastName());

                OwnerDataDto ownerData = response.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class);
                OwnerDataDto editedOwnerData = edited.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class);
                assertEquals(editedOwnerData.getAddress(), ownerData.getAddress());
            }
        }

        @Nested
        class SignatureMismatch {
            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidETag() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = "eTag";

                given()
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithChangedVersion() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                dto.setVersion(-1L);

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithChangedAccessLevelVersion() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    level.setVersion(-1L);
                }

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithAddedAccessLevel() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                dto.getAccessLevels().add(new AdminDataDto());

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithRemovedAccessLevel() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                OwnerDataDto ownerDataDto = null;
                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof OwnerDataDto dataDto) {
                        ownerDataDto = dataDto;
                    }
                }
                dto.getAccessLevels().remove(ownerDataDto);

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }
        }

        @Nested
        class Constraints {
            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithoutIfMatch() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                given()
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithoutBody() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                String etag = response.getHeader("ETag");

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Test
            void shouldReturnSC409WhenSettingExistingLicenseNumber() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof ManagerDataDto managerData) {
                        managerData.setLicenseNumber("9566541");
                    }
                }

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.CONFLICT.getStatusCode());
            }

            @Test
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidPersonalData() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                // firstName
                dto.setFirstName("");

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                // lastName
                dto.setLastName("");

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                // accountVersion
                dto.setVersion(null);

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @ParameterizedTest
            @NullAndEmptySource
            void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidLicenseNumber(String value) {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof ManagerDataDto managerData) {
                        managerData.setLicenseNumber(value);
                    }
                }

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @Nested
            class Address {

                @ParameterizedTest
                @NullSource
                @ValueSource(strings = {"99-1511", "99-51"})
                void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidPostalCode(String postalCode) {
                    io.restassured.response.Response response = given().spec(testSpec)
                        .when()
                        .get(ownProfileURL)
                        .thenReturn();
                    OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                    String etag = response.getHeader("ETag");

                    for (AccessLevelDto level : dto.getAccessLevels()) {
                        if (level instanceof ManagerDataDto managerData) {
                            AddressDto addressDto = new AddressDto(
                                "99-1511",
                                "Łęczyca",
                                "Belwederska",
                                12);
                            managerData.setAddress(addressDto);
                        }
                    }

                    given()
                        .header("If-Match", etag)
                        .spec(testSpec)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(dto)
                        .put(ownProfileURL)
                        .then()
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                }

                @ParameterizedTest
                @ValueSource(ints = {-1, 0})
                @NullSource
                void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidBuildingNumber(Integer buildingNumber) {
                    io.restassured.response.Response response = given().spec(testSpec)
                        .when()
                        .get(ownProfileURL)
                        .thenReturn();
                    OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                    String etag = response.getHeader("ETag");

                    // buildingNumber negative
                    for (AccessLevelDto level : dto.getAccessLevels()) {
                        if (level instanceof ManagerDataDto managerData) {
                            AddressDto addressDto = new AddressDto(
                                "99-151",
                                "Łęczyca",
                                "Belwederska",
                                buildingNumber);
                            managerData.setAddress(addressDto);
                        }
                    }

                    given()
                        .header("If-Match", etag)
                        .spec(testSpec)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(dto)
                        .put(ownProfileURL)
                        .then()
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                }

                @ParameterizedTest
                @ValueSource(strings = {"Ł",
                    "ŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczycaŁęczyca"})
                @NullSource
                void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidCity(String city) {
                    io.restassured.response.Response response = given().spec(testSpec)
                        .when()
                        .get(ownProfileURL)
                        .thenReturn();
                    OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                    String etag = response.getHeader("ETag");

                    for (AccessLevelDto level : dto.getAccessLevels()) {
                        if (level instanceof ManagerDataDto managerData) {
                            AddressDto addressDto = new AddressDto(
                                "99-151",
                                city,
                                "Belwederska",
                                12);
                            managerData.setAddress(addressDto);
                        }
                    }

                    given()
                        .header("If-Match", etag)
                        .spec(testSpec)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(dto)
                        .put(ownProfileURL)
                        .then()
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                }

                @Test
                void shouldReturnSC400WhenEditingOwnAccountDetailsWithInvalidAddress() {
                    io.restassured.response.Response response = given().spec(testSpec)
                        .when()
                        .get(ownProfileURL)
                        .thenReturn();
                    OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                    String etag = response.getHeader("ETag");

                    // address null
                    for (AccessLevelDto level : dto.getAccessLevels()) {
                        if (level instanceof ManagerDataDto managerData) {
                            managerData.setAddress(null);
                        }
                    }

                    given()
                        .header("If-Match", etag)
                        .spec(testSpec)
                        .when()
                        .contentType(ContentType.JSON)
                        .body(dto)
                        .put(ownProfileURL)
                        .then()
                        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                }
            }


        }

        @Nested
        class OptimisticLock {
            @Test
            void shouldReturnSC409WhenEditingOwnAccountDetailsThatChangedInTheMeantime() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                dto.setFirstName("Changed");

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.OK.getStatusCode());

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.CONFLICT.getStatusCode());
            }

            @Test
            void shouldReturnSC409WhenEditingOwnAccountDetailsWithOwnerDataChangedInTheMeantime() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof OwnerDataDto ownerData) {
                        AddressDto addressDto = new AddressDto(
                            "91-151",
                            "Łęczyca3",
                            "Belwederska3",
                            12);
                        ownerData.setAddress(addressDto);
                    }
                }

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.OK.getStatusCode());

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.CONFLICT.getStatusCode());
            }

            @Test
            void shouldReturnSC409WhenEditingOwnAccountDetailsWithManagerDataChangedInTheMeantime() {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof ManagerDataDto managerData) {
                        managerData.setLicenseNumber("654321");
                        AddressDto addressDto = new AddressDto(
                            "91-151",
                            "Łęczyca4",
                            "Belwederska4",
                            12);
                        managerData.setAddress(addressDto);
                    }
                }

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.OK.getStatusCode());

                given()
                    .header("If-Match", etag)
                    .spec(testSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put(ownProfileURL)
                    .then()
                    .contentType(ContentType.JSON)
                    .statusCode(Response.Status.CONFLICT.getStatusCode());
            }
        }

        @Nested
        class ConcurrentTests {
            @Test
            void shouldReturnSC200ForOneRequestWhenEditingPersonalData()
                throws BrokenBarrierException, InterruptedException {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                dto.setFirstName("Concurrent" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given()
                            .header("If-Match", etag)
                            .spec(testSpec)
                            .when()
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .put(ownProfileURL)
                            .getStatusCode();

                        if (statusCode == 200) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                assertEquals(1, numberOfSuccessfulAttempts.get());

                io.restassured.response.Response editedResponse = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto editedDto = editedResponse.body().as(OwnAccountDto.class);
                assertNotEquals(response.body().jsonPath().getString("firstName"), editedDto.getFirstName());
                assertEquals(dto.getFirstName(), editedDto.getFirstName());
                assertEquals(dto.getVersion() + 1, editedDto.getVersion());
            }

            @Test
            void shouldReturnSC200ForOneRequestWhenEditingOwnerData()
                throws BrokenBarrierException, InterruptedException {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                AddressDto addressDto = new AddressDto(
                    "91-151",
                    "Łęczyca" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
                    "Belwederska3",
                    12);
                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof OwnerDataDto ownerData) {
                        ownerData.setAddress(addressDto);
                    }
                }

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given()
                            .header("If-Match", etag)
                            .spec(testSpec)
                            .when()
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .put(ownProfileURL)
                            .getStatusCode();


                        if (statusCode == 200) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                assertEquals(1, numberOfSuccessfulAttempts.get());

                io.restassured.response.Response editedResponse = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();

                OwnAccountDto editedDto = editedResponse.body().as(OwnAccountDto.class);
                OwnerDataDto editedOwnerDataDto = editedResponse.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class);
                AddressDto editedAddressDto = editedOwnerDataDto.getAddress();

                assertNotEquals(response.body().jsonPath()
                        .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class).getAddress(),
                    editedAddressDto
                );
                assertEquals(addressDto, editedAddressDto);
                assertEquals(response.body().jsonPath()
                                 .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class).getVersion() +
                             1,
                    editedOwnerDataDto.getVersion());
            }

            @Test
            void shouldReturnSC200ForOneRequestWhenEditingManagerData()
                throws BrokenBarrierException, InterruptedException {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                AddressDto addressDto = new AddressDto(
                    "91-151",
                    "Łęczyca" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
                    "Belwederska4",
                    12);
                String newLicenseNumber = String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
                for (AccessLevelDto level : dto.getAccessLevels()) {
                    if (level instanceof ManagerDataDto managerData) {
                        managerData.setLicenseNumber(newLicenseNumber);
                        managerData.setAddress(addressDto);
                    }
                }

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given()
                            .header("If-Match", etag)
                            .spec(testSpec)
                            .when()
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .put(ownProfileURL)
                            .getStatusCode();


                        if (statusCode == 200) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                assertEquals(1, numberOfSuccessfulAttempts.get());

                io.restassured.response.Response editedResponse = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();

                OwnAccountDto editedDto = editedResponse.body().as(OwnAccountDto.class);
                ManagerDataDto editedManagerDataDto = editedResponse.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class);
                AddressDto editedAddressDto = editedManagerDataDto.getAddress();

                assertNotEquals(response.body().jsonPath()
                        .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class).getAddress(),
                    editedAddressDto
                );
                assertEquals(addressDto, editedAddressDto);
                assertNotEquals(response.body().jsonPath()
                        .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class).getLicenseNumber(),
                    editedManagerDataDto.getLicenseNumber()
                );
                assertEquals(newLicenseNumber, editedManagerDataDto.getLicenseNumber());

                assertEquals(response.body().jsonPath()
                                 .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class)
                                 .getVersion() + 1,
                    editedManagerDataDto.getVersion());
            }

            @Test
            void shouldReturnSC200ForAtMostTwoRequestsAndAtLeastOneRequestWhenEditingDifferentParts()
                throws BrokenBarrierException, InterruptedException {
                io.restassured.response.Response response = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto = response.body().as(OwnAccountDto.class);
                String etag = response.getHeader("ETag");

                AddressDto addressDto = new AddressDto(
                    "91-151",
                    "Łęczyca" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
                    "Belwederska4",
                    12);

                ManagerDataDto managerDataDto = response.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class);
                OwnerDataDto ownerDataDto = response.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class);

                dto.getAccessLevels().clear();
                ownerDataDto.setAddress(addressDto);
                dto.getAccessLevels().add(ownerDataDto);
                dto.getAccessLevels().add(managerDataDto);

                io.restassured.response.Response response2 = given().spec(testSpec)
                    .when()
                    .get(ownProfileURL)
                    .thenReturn();
                OwnAccountDto dto2 = response2.body().as(OwnAccountDto.class);

                managerDataDto = response.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='MANAGER'}", ManagerDataDto.class);
                ownerDataDto = response.body().jsonPath()
                    .getObject("accessLevels.find{it.level=='OWNER'}", OwnerDataDto.class);
                dto2.getAccessLevels().clear();

                String newLicenseNumber = String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));

                managerDataDto.setAddress(addressDto);
                managerDataDto.setLicenseNumber(newLicenseNumber);
                dto2.getAccessLevels().add(ownerDataDto);
                dto2.getAccessLevels().add(managerDataDto);

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    OwnAccountDto payload = (i % 2 == 0) ? dto : dto2;
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given()
                            .header("If-Match", etag)
                            .spec(testSpec)
                            .when()
                            .contentType(ContentType.JSON)
                            .body(payload)
                            .put(ownProfileURL)
                            .then()
                            .extract().statusCode();

                        if (statusCode == 200) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                int test = numberOfSuccessfulAttempts.get();
                assertTrue(1 <= test && test <= 2);
            }
        }

    }

    @Nested
    class MOK8 {

        @Test
        void shouldLogoutWithValidRefreshTokenAndInvalidateIt() {
            LoginDto loginDto = new LoginDto("pzielinski", "P@ssw0rd");

            String refreshToken = given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class))
                .extract()
                .jsonPath()
                .getString("refreshToken");

            given().spec(ownerSpec)
                .when()
                .delete("/logout?token=" + refreshToken)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            RefreshJwtDto refreshJwtDto = new RefreshJwtDto("pduda", refreshToken);
            given().body(refreshJwtDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/refresh")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body("message", Matchers.equalTo(I18n.AUTHENTICATION_EXCEPTION));
        }

        @Test
        void shouldLogoutWithNonExistentRefreshToken() {
            given().spec(ownerSpec)
                .when()
                .delete("/logout?token=" + UUID.randomUUID())
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenRefreshTokenIsNotUUID() {
            given().spec(ownerSpec)
                .when()
                .delete("/logout?token=definitelyNotUUID")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenJwtIsNotAttached() {
            given()
                .when()
                .delete("/logout?token=" + UUID.randomUUID())
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC204ForAllConcurrentRequests() throws BrokenBarrierException, InterruptedException {
            LoginDto loginDto = new LoginDto("pzielinski", "P@ssw0rd");

            String refreshToken = given()
                .contentType(ContentType.JSON)
                .body(loginDto)
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(ContentType.JSON)
                .body("jwt", Matchers.any(String.class),
                    "refreshToken", Matchers.any(String.class),
                    "language", Matchers.any(String.class))
                .extract()
                .jsonPath()
                .getString("refreshToken");

            int threadNumber = 5;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>(threadNumber);
            AtomicInteger numberFinished = new AtomicInteger();
            AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }

                    int statusCode = given().spec(ownerSpec)
                        .when()
                        .delete("/logout?token=" + refreshToken).getStatusCode();

                    if (statusCode == 204) {
                        numberOfSuccessfulAttempts.getAndIncrement();
                    }
                    numberFinished.getAndIncrement();
                }));
            }
            threads.forEach(Thread::start);
            cyclicBarrier.await();
            while (numberFinished.get() != threadNumber) {
            }

            assertEquals(numberFinished.get(), numberOfSuccessfulAttempts.get());
        }
    }

    // Wyświetl listę kont
    @Nested
    class MOK9 {
        @Test
        void shouldChangeLanguageWhenUserAuthenticatedAdmin() {
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("PL"));

            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/EN")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("EN"));

            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/PL")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("PL"));
        }

        @Test
        void shouldChangeLanguageWhenUserAuthenticatedOwner() {
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("PL"));

            given()
                .spec(ownerSpec)
                .put("accounts/me/change-language/En")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("EN"));

            given()
                .spec(ownerSpec)
                .put("accounts/me/change-language/Pl")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("PL"));
        }

        @Test
        void shouldChangeLanguageWhenUserAuthenticatedManager() {
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("PL"));

            given()
                .spec(managerSpec)
                .put("accounts/me/change-language/en")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("EN"));

            given()
                .spec(managerSpec)
                .put("accounts/me/change-language/pl")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("language", Matchers.equalTo("PL"));
        }

        @Test
        void shouldReturnSC403WhenUserNotAuthenticated() {
            given()
                .put("accounts/me/change-language/en")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenLanguageDoesntExist() {
            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/ger")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.LANGUAGE_NOT_FOUND));

            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/1")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.LANGUAGE_NOT_FOUND));
        }
    }

    // Zmiana poziomu dostępu
    @Nested
    class MOK10 {

        private final RequestSpecification ownerAndManagerSpec = new RequestSpecBuilder()
            .addHeader("Authorization", "Bearer "
                                        + RestAssured.given().body(new LoginDto("pduda", "P@ssw0rd"))
                                            .contentType(ContentType.JSON)
                                            .when()
                                            .post("/login")
                                            .jsonPath()
                                            .get("jwt"))
            .build();

        private RequestSpecification makeSpec(String login) {
            return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer "
                                            + RestAssured.given().body(new LoginDto(login, "P@ssw0rd"))
                                                .contentType(ContentType.JSON)
                                                .when()
                                                .post("/login")
                                                .jsonPath()
                                                .get("jwt"))
                .build();
        }

        @Test
        public void shouldChangeAccessLevelWhenValidAccessTypeProvidedAndAccessLevelActiveAndVerified() {

            ChangeAccessLevelDto dto = new ChangeAccessLevelDto(OWNER);

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("accessType", Matchers.equalTo(OWNER));
        }

        @Test
        public void shouldReturnSC400WhenInvalidAccessLevelProvided1() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("adad");

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        public void shouldReturnSC400WhenInvalidAccessLevelProvided2() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("ADMINISTRATOR");

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        public void shouldReturnSC400WhenBlankAccessLevelProvided() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("");

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenAccountNotHaveProvidedAccessLevel() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto(ADMIN);

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenAccountHasInactiveProvidedAccessLevel() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto(ADMIN);

            given().spec(makeSpec("bkowalewski")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenAccountHasUnverifiedProvidedAccessLevel() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto(OWNER);

            given().spec(makeSpec("nkowalska")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenNoAccountIsLoggedIn() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto(OWNER);

            given().when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }


    }

    // Zablokuj/odblokuj konto jako manager/admin
    @Nested
    class MOK11 {

        @Test
        void shouldReturnSC403AfterChangeActiveStatusWhenNotLoggedIn() {
            given()
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());


            given()
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

        }

        @Test
        void shouldReturnSC403AfterChangeActiveStatusAsManagerWrongEndpoint() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -1, true);
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403AfterChangeActiveStatusAsAdminWrongEndpoint() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -1, true);
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403AfterChangeActiveStatusOfAdminAsManager() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -34, false);
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403AfterChangeActiveStatusOfManagerAsManager() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -33, false);
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC400AfterChangeActiveStatusAsManagerEmptyBody() {
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body("")
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400AfterChangeActiveStatusAsAdminEmptyBody() {
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body("")
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400AfterChangeActiveStatusAsManagerNotValidDTO() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto();
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400AfterChangeActiveStatusAsAdminNotValidDTO() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto();
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }


        @Test
        void shouldReturnSC404AfterChangeActiveStatusAsAdminUserNotFound() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) Integer.MAX_VALUE, true);
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .assertThat()
                .body("message", Matchers.equalTo(I18n.ACCOUNT_NOT_FOUND));
        }

        @Test
        void shouldReturnSC404AfterChangeActiveStatusAsManagerUserNotFound() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) Integer.MAX_VALUE, true);
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .assertThat()
                .body("message", Matchers.equalTo(I18n.ACCOUNT_NOT_FOUND));
        }

        @Test
        void shouldReturnSC204AndNotChangeActiveStatusWhenTheyAreTheSame() {

            boolean active1 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-32))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -32, active1);

            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            boolean active11 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-32))
                .then().extract()
                .jsonPath()
                .getString("active"));

            assertEquals(active1, active11);

        }


        @Test
        void shouldChangeActiveStatusAsManagerOnOwnerAccount() {

            boolean active = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-32))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -32, !active);

            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-32))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active)));

        }

        @Test
        void shouldChangeActiveStatusAsAdminOnAnyAccount() {

            boolean active1 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-32))
                .then().extract()
                .jsonPath()
                .getString("active"));

            boolean active2 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-33))
                .then().extract()
                .jsonPath()
                .getString("active"));

            boolean active3 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-34))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto1 = new ChangeActiveStatusDto((long) -32, !active1); // OWNER
            ChangeActiveStatusDto dto2 = new ChangeActiveStatusDto((long) -33, !active2); // MANAGER
            ChangeActiveStatusDto dto3 = new ChangeActiveStatusDto((long) -34, !active3); // ADMIN

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto1)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto2)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto3)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // -----------------------------------------------------------

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-32))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active1)));

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-33))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active2)));

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-34))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active3)));
        }

        @Test
        void shouldNotChangeActiveStatusAsManagerOnOwnAccount() {
            boolean active = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .when()
                .get("/accounts/me")
                .then().extract()
                .jsonPath()
                .getString("active"));

            long id = Long.parseLong(given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .when()
                .get("/accounts/me")
                .then().extract()
                .jsonPath()
                .getString("id"));

            ChangeActiveStatusDto dto = new ChangeActiveStatusDto(id, active);

            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            boolean active2 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .when()
                .get("/accounts/me")
                .then().extract()
                .jsonPath()
                .getString("active"));

            assertEquals(active, active2);
        }

        @Test
        void shouldNotChangeActiveStatusAsAdminOnOwnAccount() {
            boolean active = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/me")
                .then().extract()
                .jsonPath()
                .getString("active"));

            long id = Long.parseLong(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/me")
                .then().extract()
                .jsonPath()
                .getString("id"));

            ChangeActiveStatusDto dto = new ChangeActiveStatusDto(id, active);

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            boolean active2 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/me")
                .then().extract()
                .jsonPath()
                .getString("active"));

            assertEquals(active, active2);
        }

        @Test
        void shouldChangeActiveStatusAsManagerOnAccountWithNotVerifiedOrActiveManagerAndAdminAccessLevels() {
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-35))
                .then()
                .assertThat()
                .body("accessLevels[0].active", Matchers.equalTo(false))
                .body("accessLevels[0].verified", Matchers.equalTo(false))
                .body("accessLevels[1].active", Matchers.equalTo(false))
                .body("accessLevels[1].verified", Matchers.equalTo(false));


            // ------------------------------------------------------------------------

            boolean active1 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-35))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto1 = new ChangeActiveStatusDto((long) -35, !active1);

            // -------------------------------------------------------------------------

            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto1)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());


            //-------------------------------------------------------------------------------
            boolean active11 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-35))
                .then().extract()
                .jsonPath()
                .getString("active"));


            assertNotEquals(active1, active11);
        }

        @Test
        void shouldChangeActiveStatusAsAdminOnAccountWithNotVerifiedOrActiveManagerAndAdminAccessLevels() {
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-35))
                .then()
                .assertThat()
                .body("accessLevels[0].active", Matchers.equalTo(false))
                .body("accessLevels[0].verified", Matchers.equalTo(false))
                .body("accessLevels[1].active", Matchers.equalTo(false))
                .body("accessLevels[1].verified", Matchers.equalTo(false));


            // ------------------------------------------------------------------------

            boolean active1 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-35))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto1 = new ChangeActiveStatusDto((long) -35, !active1);

            // -------------------------------------------------------------------------

            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto1)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());


            //-------------------------------------------------------------------------------
            boolean active11 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-35))
                .then().extract()
                .jsonPath()
                .getString("active"));


            assertNotEquals(active1, active11);
        }

        @Test
        void shouldNotChangeActiveStatusAsManagerOnAccountWithAccessLevelOtherThanOwner() {
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-33))
                .then()
                .assertThat()
                .body("accessLevels[0].active", Matchers.equalTo(true))
                .body("accessLevels[0].verified", Matchers.equalTo(true))
                .body("accessLevels[1].active", Matchers.equalTo(true))
                .body("accessLevels[1].verified", Matchers.equalTo(true));


            // ------------------------------------------------------------------------

            boolean active1 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-33))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto1 = new ChangeActiveStatusDto((long) -33, !active1);

            // -------------------------------------------------------------------------

            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto1)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());


            //-------------------------------------------------------------------------------
            boolean active11 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-33))
                .then().extract()
                .jsonPath()
                .getString("active"));


            assertEquals(active1, active11);
        }
    }

    @Nested
    class MOK15 {

        @Test
        void shouldGetAllAccountsAsAdmin() {
            Page<AccountDto> page =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetAllAccountsAsManager() {
            Page<AccountDto> page =
                given().spec(managerSpec)
                    .when()
                    .get("/accounts")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetAllAdminsAsAdmin() {
            Page<AccountDto> page =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/admins")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetAllManagersAsAdmin() {
            Page<AccountDto> page =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/managers")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetAllOwnersAsAdmin() {
            Page<AccountDto> page =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/owners")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetAllOwnersAsManager() {
            Page<AccountDto> page =
                given().spec(managerSpec)
                    .when()
                    .get("/accounts/owners")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetUnapprovedOwnersAsManager() {
            Page<AccountDto> page =
                given().spec(managerSpec)
                    .when()
                    .get("/accounts/owners/unapproved")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldGetUnapprovedManagersAsAdmin() {
            Page<AccountDto> page =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/managers/unapproved")
                    .getBody().as(Page.class);

            assertNotNull(page);
            assertNotNull(page.getData());
            assertTrue(page.getTotalSize() >= 0);
            assertTrue(page.getCurrentPage() >= 0);
            assertTrue(page.getPageSize() >= 0);
        }

        @Test
        void shouldReturnSC403WhenGettingAllAccountsAsOwner() {
            given().spec(ownerSpec)
                .when()
                .get("/accounts")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingManagersAsManager() {
            given().spec(managerSpec)
                .when()
                .get("/accounts/managers")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAdminsAsManager() {
            given().spec(managerSpec)
                .when()
                .get("/accounts/admins")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingManagersAsOwner() {
            given().spec(ownerSpec)
                .when()
                .get("/accounts/owners")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingUnapprovedOwnersAsAdmin() {
            given().spec(adminSpec)
                .when()
                .get("/accounts/owners/unapproved")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingUnapprovedOwnersAsOwner() {
            given().spec(ownerSpec)
                .when()
                .get("/accounts/owners/unapproved")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingUnapprovedManagersAsOwner() {
            given().spec(ownerSpec)
                .when()
                .get("/accounts/managers/unapproved")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAccountsAsGuest() {
            given()
                .when()
                .get("/accounts")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingOwnersAsGuest() {
            given()
                .when()
                .get("/accounts/owners")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingManagersAsGuest() {
            given()
                .when()
                .get("/accounts/managers")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAdminsAsGuest() {
            given()
                .when()
                .get("/accounts/admins")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingUnapprovedOwnersAsGuest() {
            given()
                .when()
                .get("/accounts/owners/unapproved")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingUnapprovedManagersAsGuest() {
            given()
                .when()
                .get("/accounts/managers/unapproved")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }
    }

    @Nested
    class MOK12 {
        private static final String ACCOUNT_URL = "/accounts/%d";

        @Nested
        class GrantAdminAccessLevelTest {
            private static final String GRANT_URL = "/accounts/%d/access-levels/administrator";

            @Nested
            class GrantAdminAccessLevelPositiveTest {
                @Test
                void shouldGrantAccessLevelWhenDoesNotAlreadyExistsWithStatusCode204Test() {
                    final int id = -18;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it.level=='ADMIN'}", nullValue());

                    given(adminSpec)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.active", is(true),
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsUnverifiedWithStatusCode204Test() {
                    final int id = -19;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(false),
                            "accessLevels.find{it.level=='ADMIN'}.active", is(false));

                    given(adminSpec)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.active", is(true),
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsInactiveWithStatusCode204Test() {
                    final int id = -20;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(true),
                            "accessLevels.find{it.level=='ADMIN'}.active", is(false));

                    given(adminSpec)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.active", is(true),
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsActiveWithStatusCode204Test() {
                    final int id = -21;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(true),
                            "accessLevels.find{it.level=='ADMIN'}.active", is(true));

                    given(adminSpec)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='ADMIN'}.active", is(true),
                            "accessLevels.find{it.level=='ADMIN'}.verified", is(true));
                }
            }

            @Nested
            class GrantAdminAccessLevelForbiddenTest {
                @Test
                void shouldFailToGrantAdminAccessLevelAsManagerWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(-16))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToGrantAdminAccessLevelAsOwnerWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(-16))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToGrantAdminAccessLevelAsGuestWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(-16))
                        .then()
                        .statusCode(403);
                }
            }

            @Test
            void shouldFailToGrantAdminAccessLevelWhenAccountDoesNotExistWithStatusCode404Test() {
                given(adminSpec)
                    .when()
                    .put(GRANT_URL.formatted(-98765))
                    .then()
                    .statusCode(404)
                    .body("message", is(I18n.ACCOUNT_NOT_FOUND));
            }

            @Test
            void shouldFailToGrantAdminAccessLevelToOwnAccountWithStatusCode403Test() {
                given(adminSpec)
                    .when()
                    .put(GRANT_URL.formatted(-6))
                    .then()
                    .statusCode(403)
                    .body("message", is(I18n.ACCESS_MANAGEMENT_SELF));
            }
        }

        @Nested
        class GrantManagerAccessLevelTest {
            private static final String GRANT_URL = "/accounts/%d/access-levels/manager";
            private static AddressDto addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
            private static AddManagerAccessLevelDto dto;

            @Nested
            class GrantManagerAccessLevelPositiveTest {
                private static String license;

                @BeforeAll
                static void setup() {
                    addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
                }

                @BeforeEach
                void createDto() {
                    license = generateRandomString();
                    dto = new AddManagerAccessLevelDto(license, addressDto);
                }

                @Test
                void shouldGrantAccessLevelWhenDoesNotAlreadyExistsWithStatusCode204Test() {
                    final int id = -18;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it.level=='MANAGER'}", nullValue());

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(dto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.active", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.licenseNumber", is(license),
                            "accessLevels.find{it.level=='MANAGER'}.address.postalCode", is(addressDto.postalCode()),
                            "accessLevels.find{it.level=='MANAGER'}.address.city", is(addressDto.city()),
                            "accessLevels.find{it.level=='MANAGER'}.address.street", is(addressDto.street()),
                            "accessLevels.find{it.level=='MANAGER'}.address.buildingNumber",
                            is(addressDto.buildingNumber()));
                }

                @Test
                void shouldGrantAccessLevelWhenIsUnverifiedWithStatusCode204Test() {
                    final int id = -19;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(false),
                            "accessLevels.find{it.level=='MANAGER'}.active", is(false));

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(dto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.active", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsInactiveWithStatusCode204Test() {
                    final int id = -20;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.active", is(false));

                    given(adminSpec)
                        .body(dto)
                        .contentType(ContentType.JSON)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.active", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsActiveWithStatusCode204Test() {
                    final int id = -21;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.active", is(true));

                    given(adminSpec)
                        .body(dto)
                        .contentType(ContentType.JSON)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='MANAGER'}.active", is(true),
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true));
                }
            }

            @Nested
            class GrantManagerAccessLevelForbiddenTest {
                @Test
                void shouldFailToGrantManagerAccessLevelAsManagerWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(-16))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToGrantManagerAccessLevelAsOwnerWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(-16))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToGrantManagerAccessLevelAsGuestWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(-16))
                        .then()
                        .statusCode(403);
                }
            }

            @Test
            void shouldFailToGrantManagerAccessLevelWhenAccountDoesNotExistWithStatusCode404Test() {
                dto = new AddManagerAccessLevelDto(generateRandomString(), addressDto);
                given(adminSpec)
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .when()
                    .put(GRANT_URL.formatted(-98765))
                    .then()
                    .statusCode(404)
                    .body("message", is(I18n.ACCOUNT_NOT_FOUND));
            }

            @Nested
            class GrantManagerAccessLevelValidationTest {
                private static AddressDto invalidAddressDto;

                @Test
                void shouldFailToGrantManagerAccessLevelWithoutPayloadWithStatusCode400Test() {
                    given(adminSpec)
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.JSON);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "  "})
                void shouldFailToGrantManagerAccessLevelWithLicenseNumberConstraintViolationWithStatusCode400Test(
                    String licenseNumber) {
                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(licenseNumber, addressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.JSON);
                }

                @ParameterizedTest
                @NullSource
                @ValueSource(ints = {-1, 0})
                void shouldFailToGrantManagerAccessLevelWithBuildingNumberConstraintViolationWithStatusCode400Test(
                    Integer buildingNo) {
                    invalidAddressDto = new AddressDto("12-321", "Łódź", "Wólczańska", buildingNo);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.JSON);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {
                    " ", "  ", "a",
                    "VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongCityNameExceedingLimit"})
                void shouldFailToGrantManagerAccessLevelWithCityConstraintViolationWithStatusCode400Test(String city) {
                    invalidAddressDto = new AddressDto("12-321", city, "Wólczańska", 123);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.JSON);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {
                    " ", "VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongStreetNameExceedingLimit"})
                void shouldFailToGrantManagerAccessLevelWithStreetConstraintViolationWithStatusCode400Test(
                    String street) {
                    invalidAddressDto = new AddressDto("12-321", "Łódź", street, 123);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.JSON);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "      ", "12345", "1234567"})
                void shouldFailToGrantManagerAccessLevelWithZipCodeConstraintViolationWithStatusCode400Test(
                    String zipCode) {
                    invalidAddressDto = new AddressDto(zipCode, "Łódź", "Politechniki", 1);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.JSON);
                }
            }

            @Test
            void shouldFailToGrantManagerAccessLevelWithAlreadyTakenLicenseNumberWithStatusCode409Test() {
                int statusCode = given(adminSpec)
                    .contentType(ContentType.JSON)
                    .body(new AddManagerAccessLevelDto("11111111", addressDto))
                    .when()
                    .put(GRANT_URL.formatted(-18))
                    .then()
                    .contentType(ContentType.JSON)
                    .extract().statusCode();

                assertTrue(statusCode != 204);
            }

            @Test
            void shouldFailToGrantManagerAccessLevelToOwnAccountWithStatusCode403Test() {
                given(adminSpec)
                    .contentType(ContentType.JSON)
                    .body(new AddManagerAccessLevelDto(generateRandomString(), addressDto))
                    .when()
                    .put(GRANT_URL.formatted(-6))
                    .then()
                    .statusCode(403)
                    .body("message", is(I18n.ACCESS_MANAGEMENT_SELF));
            }

            @Test
            void shouldGrantManagerAccessLevelPerformOneConcurrentChangeTest()
                throws BrokenBarrierException, InterruptedException {
                final int threads = 10;

                AtomicInteger successCount = new AtomicInteger();
                AtomicInteger finishedCount = new AtomicInteger();
                AtomicBoolean accessLevelGrantedMessage = new AtomicBoolean();

                List<Thread> threadList = new ArrayList<>(threads);

                CyclicBarrier startBarrier = new CyclicBarrier(threads + 1);
                CyclicBarrier endBarrier = new CyclicBarrier(threads + 1);

                for (int i = 0; i < threads; i++) {
                    int finalI = i;
                    threadList.add(new Thread(() -> {
                        AddressDto address = new AddressDto(
                            "93-300", "Łódź" + finalI, "Wólczańska" + finalI, finalI + 1);

                        AddManagerAccessLevelDto dto = new AddManagerAccessLevelDto(generateRandomString(), address);

                        try {
                            startBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given(adminSpec)
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .when()
                            .put(GRANT_URL.formatted(-37))
                            .then()
                            .extract()
                            .statusCode();

                        if (statusCode == 204) {
                            successCount.getAndIncrement();
                        } else if (statusCode == 409) {
                            accessLevelGrantedMessage.set(true);
                        }

                        finishedCount.getAndIncrement();
                        try {
                            endBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                    }));
                }
                threadList.forEach(Thread::start);
                startBarrier.await();
                endBarrier.await();

                assertTrue(successCount.get() >= 1);
            }

            private static String generateRandomString() {
                return UUID.randomUUID().toString().replace("-", "");
            }
        }

        @Nested
        class GrantOwnerAccessLevelTest {
            private static final String GRANT_URL = "/accounts/%d/access-levels/owner";
            private static AddressDto addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
            private static AddOwnerAccessLevelDto ownerDto;

            @Nested
            class GrantOwnerAccessLevelPositiveTest {
                @BeforeAll
                static void setup() {
                    addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
                    ownerDto = new AddOwnerAccessLevelDto(addressDto);
                }

                @Test
                void shouldGrantAccessLevelWhenDoesNotAlreadyExistsWithStatusCode204Test() {
                    final int id = -18;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it.level=='OWNER'}", nullValue());

                    given(managerSpec)
                        .contentType(ContentType.JSON)
                        .body(ownerDto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.active", is(true),
                            "accessLevels.find{it.level=='OWNER'}.verified", is(true),
                            "accessLevels.find{it.level=='OWNER'}.address.postalCode", is(addressDto.postalCode()),
                            "accessLevels.find{it.level=='OWNER'}.address.city", is(addressDto.city()),
                            "accessLevels.find{it.level=='OWNER'}.address.street", is(addressDto.street()),
                            "accessLevels.find{it.level=='OWNER'}.address.buildingNumber",
                            is(addressDto.buildingNumber()));
                }

                @Test
                void shouldGrantAccessLevelWhenIsUnverifiedWithStatusCode204Test() {
                    final int id = -19;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.verified", is(false),
                            "accessLevels.find{it.level=='OWNER'}.active", is(false));

                    given(managerSpec)
                        .contentType(ContentType.JSON)
                        .body(ownerDto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.active", is(true),
                            "accessLevels.find{it.level=='OWNER'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsInactiveWithStatusCode204Test() {
                    final int id = -20;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.verified", is(true),
                            "accessLevels.find{it.level=='OWNER'}.active", is(false));

                    given(managerSpec)
                        .body(ownerDto)
                        .contentType(ContentType.JSON)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.active", is(true),
                            "accessLevels.find{it.level=='OWNER'}.verified", is(true));
                }

                @Test
                void shouldGrantAccessLevelWhenIsActiveWithStatusCode204Test() {
                    final int id = -21;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.verified", is(true),
                            "accessLevels.find{it.level=='OWNER'}.active", is(true));

                    given(managerSpec)
                        .body(ownerDto)
                        .contentType(ContentType.JSON)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(204)
                        .body(is(""));

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it.level=='OWNER'}.active", is(true),
                            "accessLevels.find{it.level=='OWNER'}.verified", is(true));
                }
            }

            @Nested
            class GrantOwnerAccessLevelForbiddenTest {
                private final int id = -18;

                @BeforeAll
                static void setup() {
                    addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
                    ownerDto = new AddOwnerAccessLevelDto(addressDto);
                }

                @Test
                void shouldFailToAddOwnerAccessLevelAsAdminWithStatusCode403Test() {
                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(ownerDto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToAddOwnerAccessLevelAsOwnerWithStatusCode403Test() {
                    given(ownerSpec)
                        .contentType(ContentType.JSON)
                        .body(ownerDto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToAddOwnerAccessLevelAsGuestWithStatusCode403Test() {
                    given()
                        .contentType(ContentType.JSON)
                        .body(ownerDto)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }
            }

            @Test
            void shouldFailToGrantOwnerAccessLevelWhenAccountDoesNotExistWithStatusCode404Test() {
                given(managerSpec)
                    .contentType(ContentType.JSON)
                    .body(new AddOwnerAccessLevelDto(addressDto))
                    .when()
                    .put(GRANT_URL.formatted(-98765))
                    .then()
                    .statusCode(404);
            }

            @Nested
            class GrantOwnerAccessLevelConstraintViolationTest {
                private static final int id = -18;
                private static AddressDto invalidAddressDto;

                @Test
                void shouldFailToGrantOwnerAccessLevelWithoutPayloadTest() {
                    given(managerSpec)
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(400);
                }

                @ParameterizedTest
                @NullSource
                @ValueSource(ints = {0, -1})
                void shouldFailToGrantOwnerAccessLevelWithInvalidBuildingNumberWithStatusCode400Test(Integer bn) {
                    invalidAddressDto = new AddressDto("12-345", "Warszawa", "Wyścigowa", bn);

                    given(managerSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddOwnerAccessLevelDto(invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(400);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "  ", "A",
                    "VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongCityNameExceedingLimit"})
                void shouldFailToGrantOwnerAccessLevelWithInvalidCityWithStatusCode400Test(String city) {
                    invalidAddressDto = new AddressDto("12-345", city, "Wyścigowa", 5428);

                    given(managerSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddOwnerAccessLevelDto(invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(400);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "  ",
                    "VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongStreetNameExceedingLimit"})
                void shouldFailToGrantOwnerAccessLevelWithInvalidStreetWithStatusCode400Test(String street) {
                    invalidAddressDto = new AddressDto("12-345", "Poznań", street, 5428);

                    given(managerSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddOwnerAccessLevelDto(invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(400);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "      ", "12345", "1234567"})
                void shouldFailToGrantOwnerAccessLevelWithInvalidZipCodeWithStatusCode400Test(String zipCode) {
                    invalidAddressDto = new AddressDto(zipCode, "Poznań", "Prosta", 5428);

                    given(managerSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddOwnerAccessLevelDto(invalidAddressDto))
                        .when()
                        .put(GRANT_URL.formatted(id))
                        .then()
                        .statusCode(400);
                }
            }

            @Test
            void shouldFailToGrantOwnerAccessLevelToOwnAccountWithStatusCode403Test() {
                given(managerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(new AddOwnerAccessLevelDto(addressDto))
                    .then()
                    .statusCode(403)
                    .contentType(ContentType.JSON)
                    .body("message", is(I18n.ACCESS_MANAGEMENT_SELF));
            }

            @Test
            void shouldGrantOwnerAccessLevelPerformOneConcurrentChangeTest()
                throws BrokenBarrierException, InterruptedException {
                final int threads = 10;

                AtomicInteger successCount = new AtomicInteger();
                AtomicInteger finishedCount = new AtomicInteger();
                AtomicBoolean accessLevelGrantedMessage = new AtomicBoolean();

                List<Thread> threadList = new ArrayList<>(threads);

                CyclicBarrier startBarrier = new CyclicBarrier(threads + 1);
                CyclicBarrier endBarrier = new CyclicBarrier(threads + 1);

                for (int i = 0; i < threads; i++) {
                    int finalI = i;
                    threadList.add(new Thread(() -> {
                        AddressDto address = new AddressDto(
                            "93-300", "Łódź" + finalI, "Wólczańska" + finalI, finalI + 1);

                        AddOwnerAccessLevelDto dto = new AddOwnerAccessLevelDto(address);

                        try {
                            startBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given(managerSpec)
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .when()
                            .put(GRANT_URL.formatted(-37))
                            .then()
                            .extract()
                            .statusCode();

                        if (statusCode == 204) {
                            successCount.getAndIncrement();
                        } else if (statusCode == 409) {
                            accessLevelGrantedMessage.set(true);
                        }

                        finishedCount.getAndIncrement();
                        try {
                            endBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                    }));
                }
                threadList.forEach(Thread::start);
                startBarrier.await();
                endBarrier.await();

                assertTrue(successCount.get() >= 1);
            }
        }
    }

    @Nested
    class MOK13 {
        private static final String ACCOUNT_URL = "/accounts/%d";

        @Nested
        class RevokeAdminAccessLevelTest {
            private static final String REVOKE_URL = "/accounts/%d/access-levels/administrator";

            @Nested
            class RevokeAdminAccessLevelPositiveTest {
                @Test
                void shouldRevokeAdminAccessLevelIfActiveWithStatusCode204Test() {
                    final int id = -25;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='ADMIN'}.active", is(true));

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='ADMIN'}.active", is(false));
                }

                @Test
                void shouldRevokeAdminAccessLevelIfInactiveWithStatusCode204Test() {
                    final int id = -24;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='ADMIN'}.active", is(false));

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='ADMIN'}.active", is(false));
                }

                @Test
                void shouldRevokeAdminAccessLevelIfUnverifiedWithStatusCode204Test() {
                    final int id = -23;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it->it.level=='ADMIN'}.verified", is(false),
                            "accessLevels.find{it->it.level=='ADMIN'}.active", is(false));

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it->it.level=='ADMIN'}.verified", is(true),
                            "accessLevels.find{it->it.level=='ADMIN'}.active", is(false));
                }

                @Test
                void shouldRevokeAdminAccessLevelNotChangeAnythingIfAccessLevelDoesNotExistWithStatusCode204Test() {
                    final int id = -22;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='ADMIN'}", nullValue());

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='ADMIN'}", nullValue());
                }
            }

            @Nested
            class RevokeAdminAccessLevelForbiddenTest {
                private static final int id = -25;

                @Test
                void shouldFailToRevokeAdminAccessLevelAsManagerWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToRevokeAdminAccessLevelAsOwnerWithStatusCode403Test() {
                    given(ownerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToRevokeAdminAccessLevelAsGuestWithStatusCode403Test() {
                    given()
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }
            }

            @Test
            void shouldFailToRevokeAdminAccessLevelWhenAccountDoesNotExistWithStatusCode404Test() {
                given(adminSpec)
                    .when()
                    .delete(REVOKE_URL.formatted(-98765))
                    .then()
                    .statusCode(404)
                    .body("message", is(I18n.ACCOUNT_NOT_FOUND));
            }

            @Test
            void shouldFailToRevokeAdminAccessLevelFromOwnAccountWithStatusCode403Test() {
                given(adminSpec)
                    .when()
                    .delete(REVOKE_URL.formatted(-6))
                    .then()
                    .statusCode(403)
                    .body("message", is(I18n.ACCESS_MANAGEMENT_SELF));
            }
        }

        @Nested
        class RevokeManagerAccessLevelTest {
            private static final String REVOKE_URL = "accounts/%d/access-levels/manager";

            @Nested
            class RevokeManagerAccessLevelPositiveTest {
                @Test
                void shouldRevokeManagerAccessLevelIfActiveWithStatusCode204Test() {
                    final int id = -25;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='MANAGER'}.active", is(true));

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='MANAGER'}.active", is(false));
                }

                @Test
                void shouldRevokeManagerAccessLevelIfInactiveWithStatusCode204Test() {
                    final int id = -24;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='MANAGER'}.active", is(false));

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='MANAGER'}.active", is(false));
                }

                @Test
                void shouldRevokeManagerAccessLevelIfUnverifiedWithStatusCode204Test() {
                    final int id = -23;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it->it.level=='MANAGER'}.verified", is(false),
                            "accessLevels.find{it->it.level=='MANAGER'}.active", is(false));

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it->it.level=='MANAGER'}.verified", is(true),
                            "accessLevels.find{it->it.level=='MANAGER'}.active", is(false));
                }

                @Test
                void shouldRevokeManagerAccessLevelNotChangeAnythingIfAccessLevelDoesNotExistWithStatusCode204Test() {
                    final int id = -22;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='MANAGER'}", nullValue());

                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='MANAGER'}", nullValue());
                }
            }

            @Nested
            class RevokeManagerAccessLevelForbiddenTest {
                static final int id = -25;

                @Test
                void shouldFailToRevokeManagerAccessLevelAsManagerWithStatusCode403Test() {
                    given(managerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToRevokeManagerAccessLevelAsOwnerWithStatusCode403Test() {
                    given(ownerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToRevokeManagerAccessLevelAsGuestWithStatusCode403Test() {
                    given()
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }
            }

            @Test
            void shouldFailToRevokeManagerAccessLevelWhenAccountDoesNotExistWithStatusCode404Test() {
                given(adminSpec)
                    .when()
                    .delete(REVOKE_URL.formatted(-98765))
                    .then()
                    .statusCode(404)
                    .body("message", is(I18n.ACCOUNT_NOT_FOUND));
            }

            @Test
            void shouldFailToRevokeManagerAccessLevelFromOwnAccountWithStatusCode403Test() {
                var jwt = given()
                    .body(new LoginDto("dchmielewski", "P@ssw0rd"))
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/login")
                    .jsonPath()
                    .get("jwt");

                var managerAdminSpec = new RequestSpecBuilder()
                    .addHeader("Authorization", "Bearer " + jwt)
                    .build();

                given(managerAdminSpec)
                    .when()
                    .delete(REVOKE_URL.formatted(-5))
                    .then()
                    .statusCode(403)
                    .body("message", is(I18n.ACCESS_MANAGEMENT_SELF));
            }
        }

        @Nested
        class RevokeOwnerAccessLevelTest {
            private static final String REVOKE_URL = "accounts/%d/access-levels/owner";

            @Nested
            class RevokeOwnerAccessLevelPositiveTest {
                @Test
                void shouldRevokeOwnerAccessLevelIfActiveWithStatusCode204Test() {
                    final int id = -25;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='OWNER'}.active", is(true));

                    given(managerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='OWNER'}.active", is(false));
                }

                @Test
                void shouldRevokeOwnerAccessLevelIfInactiveWithStatusCode204Test() {
                    final int id = -24;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='OWNER'}.active", is(false));

                    given(managerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='OWNER'}.active", is(false));
                }

                @Test
                void shouldRevokeOwnerAccessLevelIfUnverifiedWithStatusCode204Test() {
                    final int id = -23;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it->it.level=='OWNER'}.verified", is(false),
                            "accessLevels.find{it->it.level=='OWNER'}.active", is(false));

                    given(managerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body(
                            "accessLevels.find{it->it.level=='OWNER'}.verified", is(true),
                            "accessLevels.find{it->it.level=='OWNER'}.active", is(false));
                }

                @Test
                void shouldRevokeOwnerAccessLevelNotChangeAnythingIfAccessLevelDoesNotExistWithStatusCode204Test() {
                    final int id = -22;

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='OWNER'}", nullValue());

                    given(managerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(204);

                    given(adminSpec)
                        .when()
                        .get(ACCOUNT_URL.formatted(id))
                        .then()
                        .statusCode(200)
                        .body("accessLevels.find{it->it.level=='OWNER'}", nullValue());
                }
            }

            @Nested
            class RevokeOwnerAccessLevelForbiddenTest {
                static final int id = -25;

                @Test
                void shouldFailToRevokeOwnerAccessLevelAsAdminWithStatusCode403Test() {
                    given(adminSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToRevokeOwnerAccessLevelAsOwnerWithStatusCode403Test() {
                    given(ownerSpec)
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }

                @Test
                void shouldFailToRevokeOwnerAccessLevelAsGuestWithStatusCode403Test() {
                    given()
                        .when()
                        .delete(REVOKE_URL.formatted(id))
                        .then()
                        .statusCode(403);
                }
            }

            @Test
            void shouldFailToRevokeOwnerAccessLevelWhenAccountDoesNotExistWithStatusCode404Test() {
                given(managerSpec)
                    .when()
                    .delete(REVOKE_URL.formatted(-98765))
                    .then()
                    .statusCode(404)
                    .body("message", is(I18n.ACCOUNT_NOT_FOUND));
            }

            @Test
            void shouldFailToRevokeOwnerAccessLevelFromOwnAccountWithStatusCode403Test() {
                given(managerSpec)
                    .when()
                    .delete(REVOKE_URL.formatted(-4))
                    .then()
                    .statusCode(403)
                    .body("message", is(I18n.ACCESS_MANAGEMENT_SELF));
            }
        }
    }

    @Nested
    class MOK16 {
        @Test
        void shouldForcefullyChangeOtherAccountsPasswordByAdmin() {

            // User can log in with P@ssw0rd

            JSONObject credentials = new JSONObject();
            credentials.put("login", "jkubiak");
            credentials.put("password", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(credentials.toString())
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());

            // User is active and verified
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-26")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(true))
                .body("verified", Matchers.equalTo(true));

            // Change user password by admin
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // User's account has become inactive
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-26")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(false))
                .body("verified", Matchers.equalTo(true));

            // Activate user in order to change if password has changed
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto();
            dto.setId(-26L);
            dto.setActive(true);

            given().contentType(ContentType.JSON)
                .spec(adminSpec)
                .body(dto)
                .when()
                .put("/accounts/admin/change-active-status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if user is active
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-26")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(true))
                .body("verified", Matchers.equalTo(true));

            // User cannot log in with the same credentials, password has changed
            given()
                .contentType(ContentType.JSON)
                .body(credentials.toString())
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenUserCurrentlyDoesntHaveActiveAdminAccessLevel() {

            // Logged in as manager
            given()
                .spec(managerSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            // Logged in as owner
            given()
                .spec(ownerSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            // Unauthorized user
            given()
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenUserTriesToForcefullyChangeOwnAccountPassword() {
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/bjaworski")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.ILLEGAL_SELF_ACTION));
        }

        @Test
        void shouldReturnSC403WhenChangingInactiveAccountsPassword() {

            // Check if account is inactive
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-10")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(false));

            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/nkowalska")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.INACTIVE_ACCOUNT));
        }

        @Test
        void shouldReturnSC403WhenChangingUnverifiedAccountsPassword() {

            // Check if account is unverified
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-9")
                .then()
                .assertThat()
                .body("verified", Matchers.equalTo(false));

            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/rnowak")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.UNVERIFIED_ACCOUNT));
        }

        @Test
        void shouldReturnSC404WhenAccountDoesntExist() {
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/non_existent")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.ACCOUNT_NOT_FOUND));
        }

        @Test
        void shouldReturnSC400WhenForcePasswordChangeAndTokenNotFound() {
            ResetPasswordDto resetPasswordDto = new ResetPasswordDto();
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("message", Matchers.equalTo(I18n.TOKEN_NOT_FOUND));
        }

        @Test
        void shouldReturnSC400WhenForcePasswordChangeAndWrongParameters() {
            ResetPasswordDto resetPasswordDto = new ResetPasswordDto();

            // Empty token

            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .contentType(ContentType.JSON);
            // Empty password
            resetPasswordDto.setPassword("");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // Password should contain at least one number and one special character
            resetPasswordDto.setPassword("newPassword");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);

            // Invalid uuid
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("not-uuid");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.JSON);
        }

        @Test
        void shouldReturnSC204OnlyForOneConcurrentAdminOperation() throws BrokenBarrierException, InterruptedException {
            int threadNumber = 4;
            CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
            List<Thread> threads = new ArrayList<>();
            AtomicInteger numberFinished = new AtomicInteger();
            AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

            for (int i = 0; i < threadNumber; i++) {
                threads.add(new Thread(() -> {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }

                    int statusCode = given()
                        .spec(adminSpec)
                        .contentType(ContentType.JSON)
                        .when()
                        .put("/accounts/force-password-change/wlokietek")
                        .getStatusCode();

                    if (statusCode == 204) {
                        numberOfSuccessfulAttempts.getAndIncrement();
                    }
                    numberFinished.getAndIncrement();
                }));
            }
            threads.forEach(Thread::start);
            cyclicBarrier.await();
            while (numberFinished.get() != threadNumber) {
            }
            assertEquals(1, numberOfSuccessfulAttempts.get());
        }
    }

    // Edycja danych personalnych własnego konta
    @Nested
    class MOK17 {

        private EditAnotherPersonalDataDto makeEditPersonalDataDto(AccountDto acc) {
            EditAnotherPersonalDataDto dto = new EditAnotherPersonalDataDto();
            dto.setAccessLevels(acc.getAccessLevels());
            dto.setLastName(acc.getLastName());
            dto.setFirstName(acc.getFirstName());
            dto.setLogin(acc.getLogin());
            dto.setVersion(acc.getVersion());
            dto.setEmail(acc.getEmail());
            dto.setLanguage(Language.valueOf(acc.getLanguage()));
            return dto;
        }

        @Nested
        class PositiveCases {

            @Test
            public void shouldChangeUserFirstNameAndLastName() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setFirstName("newFirstName");
                acc.setLastName("newLastName");
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);


                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.OK.getStatusCode())
                    .body("login", Matchers.equalTo(acc.getLogin()))
                    .body("firstName", Matchers.equalTo("newFirstName"))
                    .body("lastName", Matchers.equalTo("newLastName"));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                Assertions.assertEquals(acc2, acc);
            }

            @Test
            public void shouldChangeUserEmailAndLanguage() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setEmail("newFirstName@gmail.com");
                acc.setLanguage("EN");
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);


                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.OK.getStatusCode())
                    .body("login", Matchers.equalTo(acc.getLogin()))
                    .body("email", Matchers.equalTo("newFirstName@gmail.com"))
                    .body("language", Matchers.equalTo("EN"));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                Assertions.assertEquals(acc2, acc);
            }

            @Test
            public void shouldChangeUserOwnerData() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                OwnerDataDto accessLevelDto = (OwnerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(new AddressDto("99-000", "Wrocław", "Warszawska", 99));
                acc.setAccessLevels(new HashSet<>(List.of((new OwnerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.OK.getStatusCode())
                    .body("login", Matchers.equalTo(acc.getLogin()))
                    .body("accessLevels[0].address.buildingNumber",
                        Matchers.equalTo(accessLevelDto.getAddress().buildingNumber()))
                    .body("accessLevels[0].address.city", Matchers.equalTo(accessLevelDto.getAddress().city()))
                    .body("accessLevels[0].address.street", Matchers.equalTo(accessLevelDto.getAddress().street()))
                    .body("accessLevels[0].address.postalCode",
                        Matchers.equalTo(accessLevelDto.getAddress().postalCode()));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                Assertions.assertEquals(acc2, acc);
            }

            @Test
            public void shouldChangeUserManagerData() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(new AddressDto("99-000", "Wrocław", "Warszawska", 99));
                accessLevelDto.setLicenseNumber("98765432");
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.OK.getStatusCode())
                    .body("login", Matchers.equalTo(acc.getLogin()))
                    .body("accessLevels[0].address.buildingNumber",
                        Matchers.equalTo(accessLevelDto.getAddress().buildingNumber()))
                    .body("accessLevels[0].licenseNumber", Matchers.equalTo(accessLevelDto.getLicenseNumber()))
                    .body("accessLevels[0].address.city", Matchers.equalTo(accessLevelDto.getAddress().city()))
                    .body("accessLevels[0].address.street", Matchers.equalTo(accessLevelDto.getAddress().street()))
                    .body("accessLevels[0].address.postalCode",
                        Matchers.equalTo(accessLevelDto.getAddress().postalCode()));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2, acc);
            }
        }

        @Nested
        class ConstraintCases {
            @ParameterizedTest
            @ValueSource(strings = {"", "imie"})
            public void shouldReturnSC400WhenInvalidEmailAddress(String email) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setEmail(email);
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                assertNotEquals(acc2, acc);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(strings = {"",
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"})
            public void shouldReturnSC400WhenInvalidFirstName(String name) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setLastName(name);
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                assertNotEquals(acc2, acc);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(strings = {"",
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"})
            public void shouldReturnSC400WhenInvalidLastName(String name) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setLastName(name);
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                assertNotEquals(acc2, acc);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(strings = {"", "ES", "ESD"})
            public void shouldReturnSC400WhenLanguageNotFound(String lang) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);
                JSONObject obj = new JSONObject(dto);
                obj.put("language", lang);


                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(obj)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(strings = {"", "999", "1234567", "12"})
            public void shouldReturnSC400WhenInvalidAccessLevelPostalCode(String postalCode) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(new AddressDto(postalCode, "Wrocław", "Warszawska", 99));
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(strings = {"", "1",
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"})
            public void shouldReturnSC400WhenInvalidAccessLevelCity(String str) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(new AddressDto("90-987", str, "Warszawska", 99));
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"})
            public void shouldReturnSC400WhenInvalidAccessLevelStreet(String str) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(new AddressDto("90-987", "Wrocław", str, 99));
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @ParameterizedTest
            @ValueSource(ints = {-99, 0})
            public void shouldReturnSC400WhenInvalidAccessLevelBuildingNumber(int l) {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(new AddressDto("90-987", "Wrocław", "Zielona", l));
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }
        }

        @Nested
        class SignatureCases {

            @Test
            public void shouldReturnSC400WhenInvalidSignatureLogin() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setLogin("newLogin1");
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                    .body("message", Matchers.equalTo(I18n.SIGNATURE_MISMATCH));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                assertNotEquals(acc2.getLogin(), acc.getLogin());
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @Test
            public void shouldReturnSC400WhenInvalidSignatureVersion() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setVersion(acc.getVersion() + 1);
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                    .body("message", Matchers.equalTo(I18n.SIGNATURE_MISMATCH));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion() - 1);

            }

            @Test
            public void shouldReturnSC400WhenInvalidSignatureAccessLevelId() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setId(accessLevelDto.getId() + 1);
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                    .body("message", Matchers.equalTo(I18n.SIGNATURE_MISMATCH));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
            }

            @Test
            public void shouldReturnSC400WhenInvalidSignatureAccessLevelVersion() {
                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                ManagerDataDto accessLevelDto = (ManagerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setVersion(accessLevelDto.getVersion() + 1);
                acc.setAccessLevels(new HashSet<>(List.of((new ManagerDataDto[] {accessLevelDto}))));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                given().spec(adminSpec).when()
                    .header(new Header("If-Match", resp.getHeader("ETag")))
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .put("/accounts/admin/edit-other")
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                    .body("message", Matchers.equalTo(I18n.SIGNATURE_MISMATCH));

                AccountDto acc2 = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16").as(AccountDto.class);
                Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
                assertNotEquals(acc2.getAccessLevels().stream().findFirst().get().getVersion(),
                    acc.getAccessLevels().stream().findFirst().get().getVersion());
            }
        }

        @Nested
        class ConcurrentTests {

            @Test
            public void shouldBeSuccessfulForOneRequestEditAccountData()
                throws BrokenBarrierException, InterruptedException {

                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-16");
                AccountDto acc = resp.as(AccountDto.class);
                acc.setFirstName("Concurrent" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given()
                            .header(new Header("If-Match", resp.getHeader("ETag")))
                            .contentType(ContentType.JSON)
                            .spec(adminSpec)
                            .when()
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .put("/accounts/admin/edit-other")
                            .getStatusCode();

                        if (statusCode == 200) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                assertEquals(1, numberOfSuccessfulAttempts.get());

                io.restassured.response.Response editedResponse = given()
                    .spec(adminSpec)
                    .when()
                    .get("/accounts/-16")
                    .thenReturn();
                AccountDto editedDto = editedResponse.body().as(AccountDto.class);
                assertNotEquals(resp.body().jsonPath().getString("firstName"), editedDto.getFirstName());
                assertEquals(dto.getFirstName(), editedDto.getFirstName());
                assertEquals(dto.getVersion() + 1, editedDto.getVersion());
            }

            @Test
            public void shouldBeSuccessfulForOneRequestEditAccessLevel()
                throws BrokenBarrierException, InterruptedException {

                io.restassured.response.Response resp = given().spec(adminSpec)
                    .when()
                    .get("/accounts/-15");
                AccountDto acc = resp.as(AccountDto.class);

                OwnerDataDto accessLevelDto = (OwnerDataDto) acc.getAccessLevels().stream().findFirst().get();
                accessLevelDto.setAddress(
                    new AddressDto("99-000", "Wrocław", "S_" + ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
                        99));
                acc.setAccessLevels(new HashSet<>(List.of((new OwnerDataDto[] {accessLevelDto}))));

                EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

                int threadNumber = 50;
                CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber + 1);
                List<Thread> threads = new ArrayList<>(threadNumber);
                AtomicInteger numberFinished = new AtomicInteger();
                AtomicInteger numberOfSuccessfulAttempts = new AtomicInteger();

                for (int i = 0; i < threadNumber; i++) {
                    threads.add(new Thread(() -> {
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }

                        int statusCode = given()
                            .header(new Header("If-Match", resp.getHeader("ETag")))
                            .contentType(ContentType.JSON)
                            .spec(adminSpec)
                            .when()
                            .contentType(ContentType.JSON)
                            .body(dto)
                            .put("/accounts/admin/edit-other")
                            .getStatusCode();

                        if (statusCode == 200) {
                            numberOfSuccessfulAttempts.getAndIncrement();
                        }
                        numberFinished.getAndIncrement();
                    }));
                }
                threads.forEach(Thread::start);
                cyclicBarrier.await();
                while (numberFinished.get() != threadNumber) {
                }

                assertEquals(1, numberOfSuccessfulAttempts.get());

                io.restassured.response.Response editedResponse = given()
                    .spec(adminSpec)
                    .when()
                    .get("/accounts/-15")
                    .thenReturn();
                AccountDto editedDto = editedResponse.body().as(AccountDto.class);
                assertNotEquals(resp.as(AccountDto.class).getAccessLevels(), editedDto.getAccessLevels());
                assertEquals(
                    ((OwnerDataDto) dto.getAccessLevels().stream().findFirst().get()).getAddress(),
                    ((OwnerDataDto) editedDto.getAccessLevels().stream().findFirst().get()).getAddress());
                assertEquals(dto.getVersion(), editedDto.getVersion());
                assertEquals(dto.getAccessLevels().stream().findFirst().get().getVersion() + 1,
                    editedDto.getAccessLevels().stream().findFirst().get().getVersion());
            }
        }

        @Test
        public void shouldReturnSC403WhenSelfEdit() {
            AccountDto me = given().spec(adminSpec)
                .when()
                .get("/accounts/me").as(AccountDto.class);

            io.restassured.response.Response resp = given().spec(adminSpec)
                .when()
                .get("/accounts/" + me.getId());
            AccountDto acc = resp.as(AccountDto.class);
            acc.setLastName("Nowakowski1");
            EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

            given().spec(adminSpec).when()
                .header(new Header("If-Match", resp.getHeader("ETag")))
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/accounts/admin/edit-other")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .body("message", Matchers.equalTo(I18n.ILLEGAL_SELF_ACTION));

            AccountDto acc2 = given().spec(adminSpec)
                .when()
                .get("/accounts/" + me.getId()).as(AccountDto.class);
            assertNotEquals(acc2, acc);
        }

        @Test
        public void shouldReturnSC403WhenManagerAccount() {

            io.restassured.response.Response resp = given().spec(adminSpec)
                .when()
                .get("/accounts/-15");
            AccountDto acc = resp.as(AccountDto.class);
            acc.setEmail("nowy@gmail.local");
            EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

            given().spec(managerSpec).when()
                .header(new Header("If-Match", resp.getHeader("ETag")))
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/accounts/admin/edit-other")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            AccountDto acc2 = given().spec(adminSpec)
                .when()
                .get("/accounts/-15").as(AccountDto.class);
            assertNotEquals(acc2, acc);
            Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
        }

        @Test
        public void shouldReturnSC403WhenOwnerAccount() {

            io.restassured.response.Response resp = given().spec(adminSpec)
                .when()
                .get("/accounts/-15");
            AccountDto acc = resp.as(AccountDto.class);
            acc.setEmail("nowy@gmail.local");
            EditAnotherPersonalDataDto dto = makeEditPersonalDataDto(acc);

            given().spec(ownerSpec).when()
                .header(new Header("If-Match", resp.getHeader("ETag")))
                .contentType(ContentType.JSON)
                .body(dto)
                .put("/accounts/admin/edit-other")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            AccountDto acc2 = given().spec(adminSpec)
                .when()
                .get("/accounts/-15").as(AccountDto.class);
            assertNotEquals(acc2, acc);
            Assertions.assertEquals(acc2.getVersion(), acc.getVersion());
        }
    }

    @Nested
    class TwoFactorAuthentication {

        @Test
        void shouldChangeTwoFactorAuthStatusWhenUserAuthenticatedAsAdmin() {

            // With parameter
            // Check if user has two factor auth disabled
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Enable two factor auth
            given()
                .queryParam("status", true)
                .spec(adminSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Change back to disabled
            given()
                .queryParam("status", false)
                .spec(adminSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check again if change was made
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));
        }

        @Test
        void shouldChangeTwoFactorAuthStatusWhenUserAuthenticatedAsManager() {

            // With parameter
            // Check if user has two factor auth disabled
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Enable two factor auth
            given()
                .queryParam("status", true)
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Change back to disabled
            given()
                .queryParam("status", false)
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check again if change was made
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));
        }

        @Test
        void shouldChangeTwoFactorAuthStatusWhenUserAuthenticatedAsOwner() {

            // With parameter
            // Check if user has two factor auth disabled
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Enable two factor auth
            given()
                .queryParam("status", true)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Change back to disabled
            given()
                .queryParam("status", false)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldActivateTwoFactorAuthStatusWhenUserAuthenticatedAsAdminAndParameterIsWrong() {
            // Without parameter
            // Change to enabled without providing parameter
            given()
                .spec(adminSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Return user back to normal
            given()
                .queryParam("status", false)
                .spec(adminSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Wrong parameter name
            given()
                .queryParam("wrong", false)
                .spec(adminSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(adminSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Return user back to normal
            given()
                .queryParam("status", false)
                .spec(adminSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldActivateTwoFactorAuthStatusWhenUserAuthenticatedAsManagerAndParameterIsWrong() {
            // Without parameter
            // Change to enabled without providing parameter
            given()
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Return user back to normal
            given()
                .queryParam("status", false)
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Wrong parameter name
            given()
                .queryParam("wrong", false)
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Return user back to normal
            given()
                .queryParam("status", false)
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldActivateTwoFactorAuthStatusWhenUserAuthenticatedAsOwnerAndParameterIsWrong() {
            // Without parameter
            // Change to enabled without providing parameter
            given()
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Return user back to normal
            given()
                .queryParam("status", false)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Wrong parameter name
            given()
                .queryParam("wrong", false)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Return user back to normal
            given()
                .queryParam("status", false)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldDisableTwoFactorAuthWhenStatusParameterPresentButWrongValueAndAuthenticatedByAdmin() {
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Parameter that is not boolean
            given()
                .queryParam("status", true)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Disable two factor auth
            given()
                .queryParam("status", "wrong")
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));
        }

        @Test
        void shouldDisableTwoFactorAuthWhenStatusParameterPresentButWrongValueAndAuthenticatedByManager() {
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Parameter that is not boolean
            given()
                .queryParam("status", true)
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Disable two factor auth
            given()
                .queryParam("status", "wrong")
                .spec(managerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(managerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));
        }

        @Test
        void shouldDisableTwoFactorAuthWhenStatusParameterPresentButWrongValueAndAuthenticatedByOwner() {
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));

            // Parameter that is not boolean
            given()
                .queryParam("status", true)
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            // Check if change was made
            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(true));

            // Disable two factor auth
            given()
                .queryParam("status", "wrong")
                .spec(ownerSpec)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .get("accounts/me")
                .then()
                .assertThat()
                .body("twoFactorAuth", Matchers.equalTo(false));
        }

        @Test
        void shouldReturnSC403WhenUserNotAuthenticated() {
            given()
                .queryParam("status", true)
                .put("accounts/me/change_two_factor_auth_status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC202When2FAIsEnabled() {
            LoginDto loginDto = new LoginDto("bbezpieczny", "P@ssw0rd");

            given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .then()
                .assertThat()
                .statusCode(Response.Status.ACCEPTED.getStatusCode());
        }
    }
}
