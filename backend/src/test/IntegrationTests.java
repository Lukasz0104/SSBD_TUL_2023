import static io.restassured.RestAssured.given;

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
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.ChangeEmailDto;
import pl.lodz.p.it.ssbd2023.ssbd05.utils.I18n;

import java.util.UUID;

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
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -6, true);
            given()
                .contentType(ContentType.JSON)
                .spec(managerSpec)
                .body(dto)
                .when()
                .put("/accounts/manager/change-active-status")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        //fixme potrzebny user z odblokowanymi uprawnieniami managera
        @Test
        void shouldReturnSC400AfterChangeActiveStatusOfManagerAsManager() {
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -5, true);
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
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) 1, true);
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
            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) 1, true);
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
        void shouldChangeActiveStatusAsManagerOnOwnerAccount() {

            boolean active = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-2))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto = new ChangeActiveStatusDto((long) -2, !active);

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
                .get("/accounts/%s".formatted(-2))
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
                .get("/accounts/%s".formatted(-1))
                .then().extract()
                .jsonPath()
                .getString("active"));

            boolean active2 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-2))
                .then().extract()
                .jsonPath()
                .getString("active"));

            boolean active3 = Boolean.parseBoolean(given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-5))
                .then().extract()
                .jsonPath()
                .getString("active"));

            ChangeActiveStatusDto dto1 = new ChangeActiveStatusDto((long) -1, !active1); //ADMIN
            ChangeActiveStatusDto dto2 = new ChangeActiveStatusDto((long) -2, !active2); //OWNER
            ChangeActiveStatusDto dto3 = new ChangeActiveStatusDto((long) -5, !active3); //MANAGER

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
                .get("/accounts/%s".formatted(-1))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active1)));

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-2))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active2)));

            given()
                .contentType(ContentType.JSON)
                .spec(adminSpec)
                .when()
                .get("/accounts/%s".formatted(-5))
                .then()
                .assertThat()
                .body("active", Matchers.not(Matchers.equalTo(active3)));
        }
    }
}

