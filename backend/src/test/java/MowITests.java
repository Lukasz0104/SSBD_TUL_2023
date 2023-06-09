import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.lodz.p.it.ssbd2023.ssbd05.entities.mow.AccountingRule;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.CreateRateDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.request.EditPlaceDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.CategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceCategoryDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.PlaceDto;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RateDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.RatePublicDTO;
import pl.lodz.p.it.ssbd2023.ssbd05.mow.cdi.endpoint.dto.response.ReadingDto;
import pl.lodz.p.it.ssbd2023.ssbd05.shared.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

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
    class MOW13 {
        private static final String createRateUrl = "/rates";
        private static final String ratesFromCategoryUrl = "/categories/%d/rates";
        private static RequestSpecification testSpec;
        private static Long ratesNumber;
        private static String testDto;

        private static LocalDate testDate;

        static Long getRatesNumber() {
            return given().spec(testSpec).when().get(ratesFromCategoryUrl.formatted(1)).getBody().as(Page.class)
                .getTotalSize();
        }

        static void checkRatesNumber(Long expected) {
            Long ratesNumberAfter = getRatesNumber();
            assertEquals(expected, ratesNumberAfter);
            ratesNumber = ratesNumberAfter;
        }

        static String convertDtoToString(CreateRateDto createRateDto) {
            JSONObject dto = new JSONObject();
            dto.put("accountingRule", createRateDto.getAccountingRule());
            dto.put("effectiveDate", createRateDto.getEffectiveDate().toString());
            dto.put("value", createRateDto.getValue().toString());
            dto.put("categoryId", createRateDto.getCategoryId());
            return dto.toString();
        }

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

            ratesNumber = getRatesNumber();

            testDate = LocalDate.of(LocalDate.now().getYear() + 1, LocalDate.now().getMonth().getValue() + 1, 1);
            testDto = convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), testDate,
                BigDecimal.valueOf(123.51), 1L));
        }

        @Test
        void shouldPassCreatingNewRate() {
            given().spec(testSpec)
                .contentType(ContentType.JSON)
                .body(testDto)
                .when()
                .post(createRateUrl)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
            checkRatesNumber(ratesNumber + 1);
        }

        @Test
        void shouldReturnSC400WhenCreatingRateWithCategoryIdThatDoesNotExist() {
            String localTestDto =
                convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), testDate,
                    BigDecimal.valueOf(123.51), -1L));
            given().spec(testSpec)
                .contentType(ContentType.JSON)
                .body(localTestDto)
                .when()
                .post(createRateUrl)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            checkRatesNumber(ratesNumber);
        }

        @Nested
        class AuthTest {
            @Test
            void shouldReturnSC403WhenCreatingNewRateAsAdmin() {
                given().spec(adminSpec).body(testDto).when().post(createRateUrl).then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @Test
            void shouldReturnSC403WhenCreatingNewRateAsOwner() {
                given().spec(ownerSpec).body(testDto).when().post(createRateUrl).then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @Test
            void shouldReturnSC403WhenCreatingNewRateAsGuest() {
                given().body(testDto).when().post(createRateUrl).then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
                checkRatesNumber(ratesNumber);
            }
        }

        @Test
        void shouldCreateOnlyOneRateWhenConcurrent() throws BrokenBarrierException, InterruptedException {
            String localTestDto =
                convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), testDate.plusYears(3),
                    BigDecimal.valueOf(123.51), 1L));

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

                    int statusCode = given().spec(testSpec)
                        .contentType(ContentType.JSON)
                        .body(localTestDto)
                        .when()
                        .post(createRateUrl)
                        .getStatusCode();

                    if (statusCode == Response.Status.NO_CONTENT.getStatusCode()) {
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

            checkRatesNumber(ratesNumber + 1);
        }

        @Nested
        class Constraints {
            @Test
            void shouldReturnSC409WhenCreatingNotUniqueNewRate() {
                String localTestDto =
                    convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), testDate.plusYears(1),
                        BigDecimal.valueOf(123.51), 1L));
                given().spec(testSpec)
                    .contentType(ContentType.JSON)
                    .body(localTestDto)
                    .when()
                    .post(createRateUrl)
                    .then()
                    .statusCode(Response.Status.NO_CONTENT.getStatusCode());
                checkRatesNumber(ratesNumber + 1);

                given().spec(testSpec)
                    .contentType(ContentType.JSON)
                    .body(localTestDto)
                    .when()
                    .post(createRateUrl)
                    .then()
                    .statusCode(Response.Status.CONFLICT.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @ParameterizedTest
            @ValueSource(strings = {
                "1999-09-01",
                "2200-09-02"
            })
            void shouldReturnSC400WhenCreatingRateWithInvalidEffectiveDate(String date) {
                String localTestDto =
                    convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), LocalDate.parse(date),
                        BigDecimal.valueOf(123.51), 1L));
                given().spec(testSpec)
                    .contentType(ContentType.JSON)
                    .body(localTestDto)
                    .when()
                    .post(createRateUrl)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @ParameterizedTest
            @ValueSource(doubles = {
                -1.0
            })
            void shouldReturnSC400WhenCreatingRateWithInvalidValue(Double value) {
                String localTestDto =
                    convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), testDate,
                        BigDecimal.valueOf(value), 1L));
                given().spec(testSpec)
                    .contentType(ContentType.JSON)
                    .body(localTestDto)
                    .when()
                    .post(createRateUrl)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @ParameterizedTest
            @NullAndEmptySource
            @ValueSource(strings = {
                "INVALID"
            })
            void shouldReturnSC400WhenCreatingRateWithInvalidAccountingRule(String accountingRule) {
                String localTestDto = convertDtoToString(new CreateRateDto(accountingRule, testDate,
                    BigDecimal.valueOf(123.51), 1L));
                given().spec(testSpec)
                    .contentType(ContentType.JSON)
                    .body(localTestDto)
                    .when()
                    .post(createRateUrl)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @ParameterizedTest
            @NullSource
            void shouldReturnSC400WhenCreatingRateWithInvalidCategoryId(Long categoryId) {
                String localTestDto =
                    convertDtoToString(new CreateRateDto(AccountingRule.UNIT.toString(), testDate,
                        BigDecimal.valueOf(123.51), categoryId));
                given().spec(testSpec)
                    .contentType(ContentType.JSON)
                    .body(localTestDto)
                    .when()
                    .post(createRateUrl)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
                checkRatesNumber(ratesNumber);
            }
        }


    }

    @Nested
    class MOW14 {
        private static final String removeRateUrl = "/rates/%d";
        private static final String ratesFromCategoryUrl = "/categories/%d/rates";
        private static Long ratesNumber;

        static Long getRatesNumber() {
            return given().spec(managerSpec).when().get(ratesFromCategoryUrl.formatted(1)).getBody().as(Page.class)
                .getTotalSize();
        }

        static void checkRatesNumber(Long expected) {
            Long ratesNumberAfter = getRatesNumber();
            assertEquals(expected, ratesNumberAfter);
            ratesNumber = ratesNumberAfter;
        }

        @BeforeAll
        static void init() {
            ratesNumber = getRatesNumber();
        }

        @Test
        void shouldPassRemovingFutureRate() {
            given().spec(managerSpec)
                .when()
                .delete(removeRateUrl.formatted(11))
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
            checkRatesNumber(ratesNumber - 1);
        }

        @Test
        void shouldReturnSC204WhenRemovingRateThatDoesNotExist() {
            given().spec(managerSpec)
                .when()
                .delete(removeRateUrl.formatted(-2137))
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
            checkRatesNumber(ratesNumber);
        }

        @Test
        void shouldReturnSC409WhenRemovingRateThatIsAlreadyEffective() {
            given().spec(managerSpec)
                .when()
                .delete(removeRateUrl.formatted(1))
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode());
            checkRatesNumber(ratesNumber);
        }

        @Nested
        class AuthTest {
            @Test
            void shouldReturnSC403WhenRemovingRateAsOwner() {
                given().spec(ownerSpec)
                    .when()
                    .delete(removeRateUrl.formatted(1))
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @Test
            void shouldReturnSC403WhenRemovingRateAsAdmin() {
                given().spec(adminSpec)
                    .when()
                    .delete(removeRateUrl.formatted(1))
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
                checkRatesNumber(ratesNumber);
            }

            @Test
            void shouldReturnSC403WhenRemovingRateAsGuest() {
                given()
                    .when()
                    .delete(removeRateUrl.formatted(1))
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
                checkRatesNumber(ratesNumber);
            }
        }

    }

    @Nested
    class MOW20 {
        private static final String BASE_URL = "/buildings/%d/places";

        @Nested
        class GetPlacesInBuildingPositiveTest {

            @Test
            void shouldGetPlacesInBuildingAsManagerWithStatusCode200Test() {
                given(managerSpec)
                    .when()
                    .get(BASE_URL.formatted(1))
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(greaterThanOrEqualTo(2)));
            }

            @Test
            void shouldGetEmptyListWhenBuildingDoesNotExistWithStatusCode200Test() {
                given(managerSpec)
                    .when()
                    .get(BASE_URL.formatted(-123))
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(0));
            }
        }

        @Nested
        class GetPlacesInBuildingsForbiddenTest {

            @Test
            void shouldFailToGetPlacesInBuildingAsAdminWithStatusCode403Test() {
                given(adminSpec)
                    .when()
                    .get(BASE_URL.formatted(1))
                    .then()
                    .statusCode(403);
            }

            @Test
            void shouldFailToGetPlacesInBuildingAsOwnerWithStatusCode403Test() {
                given(ownerSpec)
                    .when()
                    .get(BASE_URL.formatted(1))
                    .then()
                    .statusCode(403);
            }

            @Test
            void shouldFailToGetPlacesInBuildingAsGuestWithStatusCode403Test() {
                given()
                    .when()
                    .get(BASE_URL.formatted(1))
                    .then()
                    .statusCode(403);
            }
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
                PlaceDto place = response.getBody().as(PlaceDto.class);
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
                PlaceDto place = response.getBody().as(PlaceDto.class);
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
                PlaceDto place = response.getBody().as(PlaceDto.class);
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
    class MOW9 {
        private static final String ownerURL = "/places/me/";
        private static final String managerURL = "/places/";

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

        @Nested
        class GetPlaceMetersPositiveTest {
            @Test
            void shouldGetPlaceMetersAsOwnerWithStatusCode200Test() {
                given(ownerSpec)
                    .when()
                    .get(ownerURL + "3/meters")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(greaterThanOrEqualTo(0)));
            }

            @Test
            void shouldGetPlaceMetersAsManagerWithStatusCode200Test() {
                given(managerSpec)
                    .when()
                    .get(managerURL + "2/meters")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("$.size()", is(greaterThanOrEqualTo(0)));
            }
        }

        @Nested
        class GetPlaceMetersNegativeTest {
            @Test
            void shouldFailToGetPlaceMetersAsGuestWithStatusCode403Test() {
                when()
                    .get(ownerURL + "/1/meters")
                    .then()
                    .statusCode(403);

                when()
                    .get(managerURL + "/1/meters")
                    .then()
                    .statusCode(403);
            }

            @Test
            void shouldFailToGetPlaceMetersAsOwnerWithStatusCode403Test() {
                given().spec(ownerSpec).when()
                    .get(managerURL + "/1/meters")
                    .then()
                    .statusCode(403);
            }

            @Test
            void shouldFailToGetPlaceMetersAsManagerWithStatusCode403Test() {
                given().spec(onlyManagerSpec).when()
                    .get(ownerURL + "/1/meters")
                    .then()
                    .statusCode(403);
            }

            @Test
            void shouldFailToGetPlaceMetersAsOwnerNotOwningPlaceWithStatusCode404Test() {
                given().spec(ownerSpec).when()
                    .get(ownerURL + "/7/meters")
                    .then()
                    .statusCode(404);
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

    @Nested
    class MOW28 {

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
            void shouldEditPlaceWhenLoggedInAsManager() {
                io.restassured.response.Response response = given().spec(onlyManagerSpec).when().get("/places/" + 1);
                PlaceDto place = response.getBody().as(PlaceDto.class);

                EditPlaceDto editDto = new EditPlaceDto(place);
                editDto.setSquareFootage(place.getSquareFootage().add(BigDecimal.TEN));
                editDto.setActive(!place.isActive());

                given().spec(onlyManagerSpec)
                    .contentType(ContentType.JSON)
                    .when().body(editDto).post("/places/" + 1)
                    .then().statusCode(Response.Status.NO_CONTENT.getStatusCode());

                PlaceDto newPlace = given().spec(onlyManagerSpec).when().get("/places/" + 1)
                    .getBody().as(PlaceDto.class);

                Assertions.assertEquals(newPlace.getPlaceNumber(), place.getPlaceNumber());
                Assertions.assertEquals(newPlace.getVersion() - 1, place.getVersion());
                Assertions.assertEquals(newPlace.getResidentsNumber(), place.getResidentsNumber());
                Assertions.assertEquals(newPlace.getId(), place.getId());
                Assertions.assertEquals(place.getSquareFootage().add(BigDecimal.TEN), newPlace.getSquareFootage());
                Assertions.assertEquals(place.isActive(), !newPlace.isActive());
            }

            @Test
            void shouldEditPlaceWhenLoggedInAsManagerAndOwner() {
                int id = 2;
                io.restassured.response.Response response = given().spec(managerOwnerSpec).when().get("/places/" + id);
                PlaceDto place = response.getBody().as(PlaceDto.class);

                EditPlaceDto editDto = new EditPlaceDto(place);
                editDto.setResidentsNumber(place.getResidentsNumber() + 90);
                given().spec(managerOwnerSpec)
                    .contentType(ContentType.JSON)
                    .when().body(editDto).post("/places/" + id)
                    .then().statusCode(Response.Status.NO_CONTENT.getStatusCode());

                PlaceDto newPlace = given().spec(managerOwnerSpec).when().get("/places/" + id)
                    .getBody().as(PlaceDto.class);

                Assertions.assertEquals(newPlace.getPlaceNumber(), place.getPlaceNumber());
                Assertions.assertEquals(newPlace.getVersion() - 1, place.getVersion());
                Assertions.assertEquals(newPlace.getResidentsNumber() - 90, place.getResidentsNumber());
                Assertions.assertEquals(newPlace.getId(), place.getId());
                Assertions.assertEquals(place.getSquareFootage(), newPlace.getSquareFootage());
                Assertions.assertEquals(place.isActive(), newPlace.isActive());
            }
        }

        @Nested
        class ConstraintCases {

            @NullSource
            @ParameterizedTest
            @ValueSource(ints = {-9, 0})
            void shouldReturn400SCWhenInvalidPlaceNumber(Integer placeNumber) {
                EditPlaceDto editDto = new EditPlaceDto(1L, 1L, placeNumber, BigDecimal.valueOf(10.9), 10, true);
                given().spec(managerOwnerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 1)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @ParameterizedTest
            @ValueSource(ints = {-9, 0})
            void shouldReturn400SCWhenInvalidSquareFootage(Integer val) {
                EditPlaceDto editDto
                    = new EditPlaceDto(1L, 1L, 1, BigDecimal.valueOf(val), 10, true);
                given().spec(managerOwnerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 1)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

            @NullSource
            @ParameterizedTest
            @ValueSource(ints = {-9, 0})
            void shouldReturn400SCWhenInvalidResidentsNumber(Integer val) {
                EditPlaceDto editDto =
                    new EditPlaceDto(1L, 1L, 1, BigDecimal.valueOf(10.8), val, true);
                given().spec(managerOwnerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 1)
                    .then()
                    .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
            }

        }

        @Nested
        class UnauthorizedCases {

            @Test
            void shouldReturn403SCWhenRequestAsOwner() {
                EditPlaceDto editDto =
                    new EditPlaceDto(1L, 1L, 1, BigDecimal.valueOf(10.8), 3, true);
                given().spec(onlyOwnerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 1)
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }

            @Test
            void shouldReturn403SCWhenRequestAsAdmin() {
                EditPlaceDto editDto =
                    new EditPlaceDto(1L, 1L, 1, BigDecimal.valueOf(10.8), 3, true);
                given().spec(onlyAdminSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 1)
                    .then()
                    .statusCode(Response.Status.FORBIDDEN.getStatusCode());
            }
        }

        @Nested
        class OtherNegativeCases {

            @Test
            void shouldReturn404SCWhenRequestingNonExistingPlaceByPath() {
                EditPlaceDto editDto =
                    new EditPlaceDto(1L, 1L, 1, BigDecimal.valueOf(10.8), 3, true);
                given().spec(onlyManagerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 90)
                    .then()
                    .statusCode(Response.Status.NOT_FOUND.getStatusCode());
            }

            @Test
            void shouldReturn409SCWhenChangingToAlreadyTakenPlaceNumber() {
                List<PlaceDto> places = Arrays.asList(
                    given(onlyManagerSpec).when().get("/buildings/1/places").as(PlaceDto[].class));
                int number = places.stream()
                    .filter((p) -> p.getId() != 1)
                    .findFirst()
                    .get()
                    .getPlaceNumber();

                EditPlaceDto editDto =
                    new EditPlaceDto(1L, 1L, number, BigDecimal.valueOf(10.8), 3, true);
                given().spec(onlyManagerSpec)
                    .when()
                    .contentType(ContentType.JSON)
                    .body(editDto)
                    .post("/places/" + 1)
                    .then()
                    .statusCode(Response.Status.CONFLICT.getStatusCode());
            }
        }
    }
}
