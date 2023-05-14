import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        credentials.put("login", "dchmielewski");
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
        }

        @Test
        void shouldReturnSC404WhenAccountDoesntExist(){
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
                .queryParam("email", "nkowalska@gmail.local")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .and().body("message", Matchers.equalTo(I18n.INACTIVE_ACCOUNT));
        }

        @Test
        void shouldReturnSC400WhenWrongRequestParamterrs() {
            given()
                .queryParam("email", "")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            given()
                .queryParam("email", "email")
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());

            given()
                .when()
                .post("accounts/reset-password-message")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }
    }

    @Nested
    class MOK9 {
        @Test
        void shouldChangeLanguageWhenUserAuthenticated() {
            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/PL")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(managerSpec)
                .put("accounts/me/change-language/pl")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/EN")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

            given()
                .spec(ownerSpec)
                .put("accounts/me/change-language/en")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

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

        @Test
        void shouldReturnSC400WhenWrongParameters() {
            given()
                .spec(adminSpec)
                .put("accounts/me/change-language/null")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC409WhenConcurrentChangesAreMadeOnAccount() {
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
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/ikaminski")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenUserCurrentlyDoesntHaveActiveAdminAccessLevel() {
            given()
                .spec(managerSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/ikaminski")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());

            given()
                .spec(ownerSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/ikaminski")
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
        void shouldReturnSC403WhenChangingUnverifiedAccountsPassword() {
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
        void shouldReturnSC400WhenWrongRequestParameters(){
            given()
                .spec(adminSpec)
                .contentType(ContentType.JSON)
                .when()
                .put("/accounts/force-password-change/null")
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
        }

        @Test
        void shouldReturnSC400WhenForcePasswordChangeAndTokenNotFound() {
        }

        @Test
        void shouldReturn400WhenForcePasswordChangeAndWrongParameters() {
        }

        @Test
        void shouldReturnSC409WhenConcurrentAccountModification(){
        }
    }
}