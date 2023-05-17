import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;

@Testcontainers
public class TestContainersSetup {
    protected static RequestSpecification ownerSpec;
    protected static RequestSpecification managerSpec;
    protected static RequestSpecification adminSpec;
    private static GenericContainer postgresContainer;

    @BeforeAll
    static void setup() {
        Network network = Network.SHARED;
        RestAssured.baseURI = "http://localhost:8080/eBok";

        postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withInitScript("data/db/init-user-db.sql")
            .withDatabaseName("ebok")
            .withUsername("ssbd05admin")
            .withPassword("admin")
            .withNetwork(network)
            .withNetworkAliases("db")
            .withReuse(true);

        postgresContainer.start();

        generateOwnerSpec();
        generateManagerSpec();
        generateAdminSpec();
    }

    @AfterAll
    static void cleanup() {
        postgresContainer.stop();
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
        LoginDto loginDto = new LoginDto("pduda", "P@ssw0rd");

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
}
