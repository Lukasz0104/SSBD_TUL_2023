import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.AccessLevel;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mok.Account;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.AppBaseException;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeAccessLevelDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangePasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.RefreshJwtDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.ejb.managers.AccountManager;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.HashGenerator;
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
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
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

    @Nested // zmiana hasła
    class MOK5 {

        @Test
        void shouldChangePasswordWhenProvidedValidNewPasswordAndValidOldPassword() throws AppBaseException {

            String oldPass = "P@ssw0rd";
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
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            JsonPath account = given().spec(adminSpec)
                .when()
                .get("/accounts/me").jsonPath();
            Assertions.assertEquals((Integer)account.get("version"), (Integer)oldAccount.get("version") + 1);

            given().spec(adminSpec).when()
                .body(new ChangePasswordDto(newPass, oldPass))
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-password")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
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
            Assertions.assertEquals(account.get("version"), (Integer)oldAccount.get("version"));
        }
    }

    @Nested
    class MOK10 {

        private final RequestSpecification ownerAndManagerSpec = new RequestSpecBuilder()
            .addHeader("Authorization", "Bearer "
                +  RestAssured.given().body( new LoginDto("pduda", "P@ssw0rd"))
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt"))
            .build();

        private RequestSpecification makeSpec(String login) {
            return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer "
                    +  RestAssured.given().body( new LoginDto(login, "P@ssw0rd"))
                    .contentType(ContentType.JSON)
                    .when()
                    .post("/login")
                    .jsonPath()
                    .get("jwt"))
                .build();
        }

        @Test
        public void shouldChangeAccessLevelWhenValidAccessTypeProvidedAndAccessLevelActiveAndVerified() {

            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("OWNER");

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("accessType", Matchers.equalTo("OWNER"));
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
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());}

        @Test
        public void shouldReturnSC403WhenAccountNotHaveProvidedAccessLevel() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("ADMIN");

            given().spec(makeSpec("pduda")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenAccountHasInactiveProvidedAccessLevel() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("ADMIN");

            given().spec(makeSpec("bkowalewski")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenAccountHasUnverifiedProvidedAccessLevel() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("OWNER");

            given().spec(makeSpec("nkowalska")).when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        public void shouldReturnSC403WhenNoAccountIsLoggedIn() {
            ChangeAccessLevelDto dto = new ChangeAccessLevelDto("OWNER");

            given().when()
                .body(dto)
                .contentType(ContentType.JSON)
                .put("/accounts/me/change-access-level")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }


    }

}
