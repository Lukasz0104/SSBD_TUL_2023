import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CategoryDTO;

import java.util.List;

public class MowITests extends TestContainersSetup {

    @Nested
    class MOW2 {
        private static final String URL = "/buildings";

        @Nested
        class GetAllBuildingsPositiveTest {
            @Test
            void shouldGetAllBuildingsAsOwnerWithStatusCode200Test() {
                given(ownerSpec)
                    .when()
                    .get(URL)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(greaterThan(0)));
            }

            @Test
            void shouldGetAllBuildingsAsManagerWithStatusCode200Test() {
                given(managerSpec)
                    .when()
                    .get(URL)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(greaterThan(0)));
            }

            @Test
            void shouldGetAllBuildingsAsAdminWithStatusCode200Test() {
                given(adminSpec)
                    .when()
                    .get(URL)
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(greaterThan(0)));
            }
        }

        @Nested
        class GetAllBuildingsAccessForbiddenTest {
            @Test
            void shouldFailToGetAllBuildingsAsGuestWithStatusCode403Test() {
                when()
                    .get(URL)
                    .then()
                    .statusCode(403);
            }
        }
    }

    @Nested
    class MOW11 {
        private static final String categoriesURL = "/categories";
        private static RequestSpecification testSpec;

        @BeforeAll
        static void generateTestSpec() {
            LoginDto loginDto = new LoginDto("kgraczyk", "P@ssw0rd");

            String jwt = given().body(loginDto)
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
        void shouldPassGettingAllCategories() {
            io.restassured.response.Response response = given().spec(testSpec).when().get(categoriesURL);
            List<CategoryDTO> categories =
                List.of(response.getBody().as(CategoryDTO[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(categories);
            assertTrue(categories.size() > 0);

        }

        @Test
        void shouldReturnSC403WhenGettingAllCategoriesAsAdmin() {
            given().spec(adminSpec).when().get(categoriesURL).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAllCategoriesAsOwner() {
            given().spec(ownerSpec).when().get(categoriesURL).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAllCategoriesAsGuest() {
            given().when().get(categoriesURL).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }
    }
}
