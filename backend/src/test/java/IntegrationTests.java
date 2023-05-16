import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.AddressDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.AddManagerAccessLevelDto;
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
        }

        @Nested
        class GrantManagerAccessLevelTest {
            private static final String GRANT_URL = "/accounts/%d/access-levels/manager";
            private static AddressDto addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
            private static AddManagerAccessLevelDto dto;

            @Nested
            class GrantManagerAccessLevelPositiveTest {

                @BeforeAll
                static void setup() {
                    addressDto = new AddressDto("93-300", "Łódź", "Wólczańska", 215);
                }

                @BeforeEach
                void createDto() {
                    dto = new AddManagerAccessLevelDto(generateRandomString(), addressDto);
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
                            "accessLevels.find{it.level=='MANAGER'}.verified", is(true));
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
                        .contentType(ContentType.HTML);
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
                        .contentType(ContentType.HTML);
                }

                @ParameterizedTest
                @NullSource
                @ValueSource(ints = {-1, 0})
                void shouldFailToGrantManagerAccessLevelWithBuildingNumberConstraintViolationWithStatusCode400Test(
                    Integer buildingNo) {
                    addressDto = new AddressDto("12-321", "Łódź", "Wólczańska", buildingNo);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), addressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.HTML);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {
                    " ", "  ", "a",
                    "VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongCityNameExceedingLimit"})
                void shouldFailToGrantManagerAccessLevelWithCityConstraintViolationWithStatusCode400Test(String city) {
                    addressDto = new AddressDto("12-321", city, "Wólczańska", 123);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), addressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.HTML);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {
                    " ", "VeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongStreetNameExceedingLimit"})
                void shouldFailToGrantManagerAccessLevelWithStreetConstraintViolationWithStatusCode400Test(
                    String street) {
                    addressDto = new AddressDto("12-321", "Łódź", street, 123);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), addressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.HTML);
                }

                @ParameterizedTest
                @NullAndEmptySource
                @ValueSource(strings = {" ", "      ", "12345", "1234567"})
                void shouldFailToGrantManagerAccessLevelWithZipCodeConstraintViolationWithStatusCode400Test(
                    String zipCode) {
                    addressDto = new AddressDto(zipCode, "Łódź", "Politechniki", 1);

                    given(adminSpec)
                        .contentType(ContentType.JSON)
                        .body(new AddManagerAccessLevelDto(generateRandomString(), addressDto))
                        .when()
                        .put(GRANT_URL.formatted(-18))
                        .then()
                        .statusCode(400)
                        .contentType(ContentType.HTML);
                }
            }

            @Test
            void shouldFailToGrantManagerAccessLevelWithAlreadyTakenLicenseNumberWithStatusCode409Test() {
                given(adminSpec)
                    .contentType(ContentType.JSON)
                    .body(new AddManagerAccessLevelDto("11111111", addressDto))
                    .when()
                    .put(GRANT_URL.formatted(-18))
                    .then()
                    .statusCode(409)
                    .contentType(ContentType.JSON)
                    .body("message", is(I18n.LICENSE_NUMBER_ALREADY_TAKEN));
            }

            // TODO add tests for race conditions

            private static String generateRandomString() {
                return UUID.randomUUID().toString().replace("-", "");
            }
        }

        @Nested
        class GrantOwnerAccessLevelTest {
            @Nested
            class GrantOwnerAccessLevelPositiveTest {

            }

            @Nested
            class GrantOwnerAccessLevelForbiddenTest {

            }
        }
    }
}
