import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RefreshJwtDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.response.AccountDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class IntegrationTests {

    protected static RequestSpecification ownerSpec;
    protected static RequestSpecification managerSpec;
    protected static RequestSpecification adminSpec;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080/eBok";

        generateOwnerSpec();
        generateManagerSpec();
        generateAdminSpec();
    }

    static void generateOwnerSpec() {
        LoginDto loginDto = new LoginDto("pzielinski", "P@ssw0rd");

        String ownerJWT = RestAssured.given().body(loginDto)
            .contentType(ContentType.JSON)
            .when()
            .post("/login")
            .jsonPath()
            .get("jwt");

        ownerSpec = new RequestSpecBuilder()
            .addHeader("Authorization", "Bearer " + ownerJWT)
            .build();
    }

    static void generateManagerSpec() {
        LoginDto loginDto = new LoginDto("dchmielewski", "P@ssw0rd");

        String managerJWT = RestAssured.given().body(loginDto)
            .contentType(ContentType.JSON)
            .when()
            .post("/login")
            .jsonPath()
            .get("jwt");

        managerSpec = new RequestSpecBuilder()
            .addHeader("Authorization", "Bearer " + managerJWT)
            .build();
    }

    static void generateAdminSpec() {
        LoginDto loginDto = new LoginDto("bjaworski", "P@ssw0rd");

        String adminJWT = RestAssured.given().body(loginDto)
            .contentType(ContentType.JSON)
            .when()
            .post("/login")
            .jsonPath()
            .get("jwt");

        adminSpec = new RequestSpecBuilder()
            .addHeader("Authorization", "Bearer " + adminJWT)
            .build();
    }


    //Zaloguj się
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

    //Wyloguj się
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

    //Wyświetl listę kont
    @Nested
    class MOK15 {

        @Test
        void shouldGetAllAccountsAsAdmin() {
            List<AccountDto> accounts =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts")
                    .getBody().as(ArrayList.class);

            assertNotNull(accounts);
            assertTrue(accounts.size() > 0);
        }

        @Test
        void shouldGetAllAccountsAsManager() {
            List<AccountDto> accounts =
                given().spec(managerSpec)
                    .when()
                    .get("/accounts")
                    .getBody().as(ArrayList.class);

            assertNotNull(accounts);
            assertTrue(accounts.size() > 0);
        }

        @Test
        void shouldGetAllAdminsAsAdmin() {
            List<AccountDto> admins =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/admins")
                    .getBody().as(ArrayList.class);

            assertNotNull(admins);
        }

        @Test
        void shouldGetAllManagersAsAdmin() {
            List<AccountDto> managers =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/managers")
                    .getBody().as(ArrayList.class);

            assertNotNull(managers);
        }

        @Test
        void shouldGetAllOwnersAsAdmin() {
            List<AccountDto> owners =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/owners")
                    .getBody().as(ArrayList.class);

            assertNotNull(owners);
        }

        @Test
        void shouldGetAllOwnersAsManager() {
            List<AccountDto> owners =
                given().spec(managerSpec)
                    .when()
                    .get("/accounts/owners")
                    .getBody().as(ArrayList.class);

            assertNotNull(owners);
        }

        @Test
        void shouldGetUnapprovedOwnersAsManager() {
            List<AccountDto> owners =
                given().spec(managerSpec)
                    .when()
                    .get("/accounts/owners/unapproved")
                    .getBody().as(ArrayList.class);

            assertNotNull(owners);
        }

        @Test
        void shouldGetUnapprovedManagersAsAdmin() {
            List<AccountDto> managers =
                given().spec(adminSpec)
                    .when()
                    .get("/accounts/managers/unapproved")
                    .getBody().as(ArrayList.class);

            assertNotNull(managers);
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

    //Zmień adres e-mail przypisany do własnego konta
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


    //Zablokuj/odblokuj konto jako manager/admin
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

            ChangeActiveStatusDto dto1 = new ChangeActiveStatusDto((long) -32, !active1); //OWNER
            ChangeActiveStatusDto dto2 = new ChangeActiveStatusDto((long) -33, !active2); //MANAGER
            ChangeActiveStatusDto dto3 = new ChangeActiveStatusDto((long) -34, !active3); //ADMIN

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
}
