import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RateDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

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

    @Nested
    class MOW12 {
        private static final String ratesFromCategoryUrl = "/categories/%d/rates";
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
        void shouldPassGettingAllRatesFromCategory() {
            io.restassured.response.Response response =
                given().spec(testSpec).when().get(ratesFromCategoryUrl.formatted(1));
            Page<RateDTO> ratePage =
                response.getBody().as(Page.class);

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(ratePage);
            assertTrue(ratePage.getTotalSize() >= 0);
            assertTrue(ratePage.getCurrentPage() >= 0);
            assertTrue(ratePage.getPageSize() >= 0);
            assertTrue(ratePage.getData().size() > 0);
        }

        @Test
        void shouldReturnEmptyPageDataWhenGettingAllRatesFromCategoryThatDoesntExist() {
            io.restassured.response.Response response =
                given().spec(testSpec).when().get(ratesFromCategoryUrl.formatted(-2137));
            Page<RateDTO> ratePage =
                response.getBody().as(Page.class);

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(ratePage);
            assertEquals(0, (long) ratePage.getTotalSize());
            assertEquals(0, ratePage.getCurrentPage());
            assertEquals(0, ratePage.getPageSize());
            assertEquals(0, ratePage.getData().size());
        }

        @Test
        void shouldReturnSC403WhenGettingAllCategoriesAsAdmin() {
            given().spec(adminSpec).when().get(ratesFromCategoryUrl.formatted(1)).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAllCategoriesAsOwner() {
            given().spec(ownerSpec).when().get(ratesFromCategoryUrl.formatted(1)).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingAllCategoriesAsGuest() {
            given().when().get(ratesFromCategoryUrl.formatted(1)).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }
    }

    @Nested
    class MOW25 {

        @Test
        void shouldReturnPlaceCategoryDtoForAllCategoriesForPlace() {
            io.restassured.response.Response response = given().spec(managerSpec).when().get("/places/1/categories");
            List<PlaceCategoryDto> categories =
                List.of(response.getBody().as(PlaceCategoryDto[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(categories);
            assertTrue(categories.size() > 0);
        }

        @Test
        void shouldReturnEmptyWhenPlaceNotFound() {
            io.restassured.response.Response response = given().spec(managerSpec).when().get("/places/-1/categories");
            List<PlaceCategoryDto> categories =
                List.of(response.getBody().as(PlaceCategoryDto[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(categories);
            assertEquals(0, categories.size());
        }

        @Test
        void shouldReturnSC404WhenPathParamIsNotANumber() {
            given().spec(managerSpec).when().get("/places/not_number/categories").then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingPlaceCategoriesForPlaceAsOwner() {
            given().spec(ownerSpec).when().get("/places/1/categories").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingPlaceCategoriesForPlaceAsAdmin() {
            given().spec(adminSpec).when().get("/places/1/categories").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingPlaceCategoriesForPlaceAsGuest() {
            given().when().get("/places/1/categories").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }
    }

    @Nested
    class MOW4 {
        private static final String ownPlacesURL = "/places/me";

        private static RequestSpecification onlyManagerSpec;

        @BeforeAll
        static void generateTestSpec() {
            LoginDto loginDto = new LoginDto("dchmielewski", "P@ssw0rd");

            String jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");

            onlyManagerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();
        }

        @Test
        void shouldPassGettingOwnPlaces() {
            io.restassured.response.Response response = given().spec(ownerSpec).when().get(ownPlacesURL);
            List<PlaceDto> places =
                List.of(response.getBody().as(PlaceDto[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(places);
        }

        @Test
        void shouldReturnSC403WhenGettingOwnPlacesAsAdmin() {
            given().spec(adminSpec).when().get(ownPlacesURL).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingOwnPlacesAsManager() {
            given().spec(onlyManagerSpec).when().get(ownPlacesURL).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }

        @Test
        void shouldReturnSC403WhenGettingOwnPlacesAsGuest() {
            given().when().get(ownPlacesURL).then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
        }
    }
}
