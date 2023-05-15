import static io.restassured.RestAssured.given;

import java.util.UUID;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeActiveStatusDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ResetPasswordDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

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
        JSONObject credentials = new JSONObject();
        credentials.put("login", "pzielinski");
        credentials.put("password", "P@ssw0rd");

        String ownerJWT = RestAssured.given().body(credentials.toString())
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
        JSONObject credentials = new JSONObject();
        credentials.put("login", "pduda");
        credentials.put("password", "P@ssw0rd");

        String managerJWT = RestAssured.given().body(credentials.toString())
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
        JSONObject credentials = new JSONObject();
        credentials.put("login", "bjaworski");
        credentials.put("password", "P@ssw0rd");

        String adminJWT = RestAssured.given().body(credentials.toString())
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
        void shouldLoginWhenCredentialsAreValid() {
            JSONObject credentials = new JSONObject();
            credentials.put("login", "pduda");
            credentials.put("password", "P@ssw0rd");

            given()
                .contentType(ContentType.JSON)
                .body(credentials.toString())
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
        }

        @Test
        void shouldFailLoginWhenAccountDoesntExists() {
            JSONObject credentials = new JSONObject();
            credentials.put("login", "randomAccount");
            credentials.put("password", "randomPassword");

            given()
                .contentType(ContentType.JSON)
                .body(credentials.toString())
                .when()
                .post("/login")
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
        }
    }

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
        void shouldReturnSC404WhenAccountDoesntExist() {
            given()
                .queryParam("email", "non_existent@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.ACCOUNT_NOT_FOUND));
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

            //Empty email
            given()
                .queryParam("email", "")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //Provided value is not an email
            given()
                .queryParam("email", "email")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //No parameter provided
            given()
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);
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
            //Invalid password, should have at least one number and one special character
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
                .assertThat().contentType(ContentType.HTML);

            //Invalid password, should have at least one number and one special character
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("not_uuid");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //Empty password
            resetPasswordDto.setPassword("");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //Empty token
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .when()
                .post("accounts/reset-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);
        }
    }

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

    //Pobierz wszystkie konta
    @Nested
    class MOK15 {
        @Test
        void shouldGetAccountsWhenIsAdmin() {
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .get("/accounts")
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
        }
    }

    @Nested
    class MOK16 {
        @Test
        void shouldForcefullyChangeOtherAccountsPasswordByAdmin() {

            //User can log in with P@ssw0rd

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

            //Change user password by admin
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            //User's account has become inactive
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-26")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(false))
                .body("verified", Matchers.equalTo(true));

            //Activate user in order to change if password has changed
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

            //Check if user is active
            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/-26")
                .then()
                .assertThat()
                .body("active", Matchers.equalTo(true))
                .body("verified", Matchers.equalTo(true));

            //User cannot log in with the same credentials, password has changed
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

            //Logged in as manager
            given()
                .spec(managerSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            //Logged in as owner
            given()
                .spec(ownerSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/jkubiak")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            //Unauthorized user
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

            //Check if account is inactive
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

            //Check if account is unverified
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

            //Empty token

            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //Empty password
            resetPasswordDto.setPassword("");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //Password should contain at least one number and one special character
            resetPasswordDto.setPassword("newPassword");
            resetPasswordDto.setToken(UUID.randomUUID().toString());
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);

            //Invalid uuid
            resetPasswordDto.setPassword("newPassword@1");
            resetPasswordDto.setToken("not-uuid");
            given()
                .contentType(ContentType.JSON)
                .body(resetPasswordDto)
                .put("/accounts/override-forced-password")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .assertThat().contentType(ContentType.HTML);
        }
    }
}