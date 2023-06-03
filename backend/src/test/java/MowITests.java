import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RateDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RatePublicDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.ReadingDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.util.List;

public class MowITests extends TestContainersSetup {

    @Nested
    class MOW1 {
        private static final String ratesUrl = "/rates";

        private static final String categoriesURL = "/categories";

        private static int categoriesNumber;

        @BeforeAll
        static void init() {
            io.restassured.response.Response response = given().spec(managerSpec).when().get(categoriesURL);
            categoriesNumber = List.of(response.getBody().as(CategoryDTO[].class)).size();

            response.then().statusCode(Response.Status.OK.getStatusCode());
        }

        @Test
        void shouldPassGettingCurrentRatesAsOwner() {
            io.restassured.response.Response response = given().spec(ownerSpec).when().get(ratesUrl);
            List<RatePublicDTO> rates =
                List.of(response.getBody().as(RatePublicDTO[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(rates);
            assertEquals(categoriesNumber, rates.size());
        }

        @Test
        void shouldPassGettingCurrentRatesAsManager() {
            io.restassured.response.Response response = given().spec(managerSpec).when().get(ratesUrl);
            List<RatePublicDTO> rates =
                List.of(response.getBody().as(RatePublicDTO[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(rates);
            assertEquals(categoriesNumber, rates.size());
        }

        @Test
        void shouldPassGettingCurrentRatesAsAdmin() {
            io.restassured.response.Response response = given().spec(adminSpec).when().get(ratesUrl);
            List<RatePublicDTO> rates =
                List.of(response.getBody().as(RatePublicDTO[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(rates);
            assertEquals(categoriesNumber, rates.size());
        }

        @Test
        void shouldPassGettingCurrentRatesAsGuest() {
            io.restassured.response.Response response = given().when().get(ratesUrl);
            List<RatePublicDTO> rates =
                List.of(response.getBody().as(RatePublicDTO[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(rates);
            assertEquals(categoriesNumber, rates.size());
        }

    }


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
            List<PlaceCategoryDTO> categories =
                List.of(response.getBody().as(PlaceCategoryDTO[].class));

            response.then().statusCode(Response.Status.OK.getStatusCode());
            assertNotNull(categories);
            assertTrue(categories.size() > 0);
        }

        @Test
        void shouldReturnEmptyWhenPlaceNotFound() {
            io.restassured.response.Response response = given().spec(managerSpec).when().get("/places/-1/categories");
            List<PlaceCategoryDTO> categories =
                List.of(response.getBody().as(PlaceCategoryDTO[].class));

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
            List<PlaceDTO> places =
                List.of(response.getBody().as(PlaceDTO[].class));

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

    @Nested
    class MOW5 {

        private static RequestSpecification onlyManagerSpec;
        private static RequestSpecification onlyAdminSpec;
        private static RequestSpecification adminOwnerSpec;
        private static RequestSpecification onlyOwnerSpec;
        private static RequestSpecification managerOwnerSpec;

        @BeforeAll
        static void generateTestSpec() {
            LoginDto loginDto = new LoginDto("wplatynowy", "P@ssw0rd");
            String jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            onlyOwnerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

            loginDto = new LoginDto("azloty", "P@ssw0rd");
            jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            onlyManagerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

            loginDto = new LoginDto("wlokietek", "P@ssw0rd");
            jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            onlyAdminSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

            loginDto = new LoginDto("asrebrna", "P@ssw0rd");
            jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            adminOwnerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

            loginDto = new LoginDto("pduda", "P@ssw0rd");
            jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            managerOwnerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();
        }

        @Nested
        class PositiveCases {
            @Test
            void shouldPassOwnerGettingOwnPlaces() {
                io.restassured.response.Response response = given().spec(onlyOwnerSpec).when().get("/places/me/" + 7);
                PlaceDTO place = response.getBody().as(PlaceDTO.class);
                response.then().statusCode(Response.Status.OK.getStatusCode());
                Assertions.assertNotNull(place);
                Assertions.assertEquals(place.getId(), 7);
                Assertions.assertEquals(place.getPlaceNumber(), 4);
                Assertions.assertEquals(place.getResidentsNumber(), 10);
                Assertions.assertEquals(place.getSquareFootage().toPlainString(), "180.000");
            }

            @Test
            void shouldPassManagerGettingOwnPlaces() {
                io.restassured.response.Response response =
                    given().spec(managerOwnerSpec).when().get("/places/me/" + 5);
                PlaceDTO place = response.getBody().as(PlaceDTO.class);
                response.then().statusCode(Response.Status.OK.getStatusCode());
                Assertions.assertNotNull(place);
                Assertions.assertEquals(place.getId(), 5);
                Assertions.assertEquals(place.getPlaceNumber(), 2);
                Assertions.assertEquals(place.getResidentsNumber(), 2);
                Assertions.assertEquals(place.getSquareFootage().toPlainString(), "68.000");
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldPassManagerGettingAnyPlaces(int id) {
                io.restassured.response.Response response = given().spec(onlyManagerSpec).when().get("/places/" + id);
                PlaceDTO place = response.getBody().as(PlaceDTO.class);
                response.then().statusCode(Response.Status.OK.getStatusCode());
                Assertions.assertNotNull(place);
            }

        }

        @Nested
        class NegativeCases {

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingOwnPlaceAsAdmin(int id) {
                given().spec(onlyAdminSpec)
                    .when()
                    .get("/places/me/" + id)
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingAnyPlaceAsAdmin(int id) {
                given().spec(onlyAdminSpec)
                    .when()
                    .get("/places/" + id)
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @Test
            void shouldReturnSC403WhenGettingNotOwnPlaceAsOwner() {
                given().spec(adminOwnerSpec)
                    .when()
                    .get("/places/me/" + 1)
                    .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());
            }


            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingAnyPlaceAsOwner(int id) {
                given().spec(adminOwnerSpec)
                    .when()
                    .get("/places/" + id)
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

        }
    }

    @Nested
    class MOW29 {

        private static RequestSpecification onlyManagerSpec;
        private static RequestSpecification onlyAdminSpec;
        private static RequestSpecification onlyOwnerSpec;

        @BeforeAll
        static void generateTestSpec() {
            LoginDto loginDto = new LoginDto("lnowicki", "P@ssw0rd");
            String jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            onlyOwnerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

            loginDto = new LoginDto("azloty", "P@ssw0rd");
            jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            onlyManagerSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();

            loginDto = new LoginDto("wlokietek", "P@ssw0rd");
            jwt = given().body(loginDto)
                .contentType(ContentType.JSON)
                .when()
                .post("/login")
                .jsonPath()
                .get("jwt");
            onlyAdminSpec = new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + jwt)
                .build();
        }

        @Nested
        class PositiveCases {
            @Test
            void shouldPassOwnerGettingOwnPlaceMeterReadings() {
                io.restassured.response.Response response =
                    given().spec(onlyOwnerSpec).when().get("/meters/me/" + 5 + "/readings");
                Page<ReadingDto> readingDtoPage =
                    response.getBody().as(Page.class);

                response.then().statusCode(Response.Status.OK.getStatusCode());
                assertNotNull(readingDtoPage);
                assertTrue(readingDtoPage.getTotalSize() >= 0);
                assertTrue(readingDtoPage.getCurrentPage() >= 0);
                assertTrue(readingDtoPage.getPageSize() >= 0);
                assertTrue(readingDtoPage.getData().size() > 0);
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldPassManagerGettingAnyMeterReadings(int id) {
                io.restassured.response.Response response =
                    given().spec(onlyManagerSpec).when().get("/meters/" + id + "/readings");
                Page<ReadingDto> readingDtoPage =
                    response.getBody().as(Page.class);

                response.then().statusCode(Response.Status.OK.getStatusCode());
                assertNotNull(readingDtoPage);
                assertTrue(readingDtoPage.getTotalSize() >= 0);
                assertTrue(readingDtoPage.getCurrentPage() >= 0);
                assertTrue(readingDtoPage.getPageSize() >= 0);
                assertTrue(readingDtoPage.getData().size() > 0);
            }

        }

        @Nested
        class NegativeCases {

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingMeterReadingsAsAdmin(int id) {
                given().spec(onlyAdminSpec)
                    .when()
                    .get("/meters/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingOwnPlaceMeterReadingsAsAdmin(int id) {
                given().spec(onlyAdminSpec)
                    .when()
                    .get("/meters/me/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingOwnPlaceMeterReadingsAsManager(int id) {
                given().spec(onlyManagerSpec)
                    .when()
                    .get("/meters/me/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
            void shouldReturnSC403WhenGettingAnyMeterReadingsAsOwner(int id) {
                given().spec(onlyOwnerSpec)
                    .when()
                    .get("/meters/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 6, 7})
            void shouldReturnSC404WhenGettingPlaceMeterReadingsAsOwnerNotOwningPlace(int id) {
                given().spec(onlyOwnerSpec)
                    .when()
                    .get("/meters/me/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {1, 2, 3, 4, 6, 7})
            void shouldReturnSC403WhenGettingAnyMeterReadingsAsGuest(int id) {
                given()
                    .when()
                    .get("/meters/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());

                given().spec(onlyAdminSpec)
                    .when()
                    .get("/meters/me/" + id + "/readings")
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }
        }
    }
}
