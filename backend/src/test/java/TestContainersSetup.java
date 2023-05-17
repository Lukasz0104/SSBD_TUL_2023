import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;
import pl.lodz.p.it.ssbd2023.ssbd05.mok.cdi.endpoint.dto.request.LoginDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/*
    W celu uruchomienia testów należy dodać pliki certyfikatu, z podowdów bezpieczeństwa nie zostały one umieszczone w
    repozytorium. Pliki potrzebne do uruchomienia to:
        - fullchain.pem
        - privkey.pem
        Należy je umięcić w katalogu: /backend/src/resources/data/gw/letsencrypt/live/team-5.proj-sum.it.p.lodz.pl/
    Oraz plik:
        - ssl-dhparams.pem
        Który należy umieścić w katalogu: /backend/src/resources/data/gw/letsencrypt/
 */

@Testcontainers
public class TestContainersSetup {
    protected static RequestSpecification ownerSpec;
    protected static RequestSpecification managerSpec;
    protected static RequestSpecification adminSpec;
    private static GenericContainer postgresContainer;
    private static GenericContainer payaraContainer;
    private static GenericContainer nginxContainer;

    @BeforeAll
    static void setup() throws IOException {
        Network network = Network.SHARED;

        postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
            .withInitScript("data/db/init-user-db.sql")
            .withDatabaseName("ebok")
            .withUsername("ssbd05admin")
            .withPassword("admin")
            .withNetwork(network)
            .withNetworkAliases("db")
            .waitingFor(Wait.forListeningPort())
            .withReuse(true);

        MountableFile warFile = MountableFile.forHostPath(
            Paths.get(new File("target/eBok.war").getCanonicalPath()).toAbsolutePath(), 0777);

        payaraContainer = new GenericContainer("payara/server-full:6.2023.2-jdk17")
            .withEnv("mail_smtp_host", "smtp.gmail.com.local")
            .withExposedPorts(8080)
            .dependsOn(postgresContainer)
            .withNetwork(network)
            .withNetworkAliases("app")
            .withCopyFileToContainer(warFile, "/opt/payara/deployments/ebok.war")
            .waitingFor(Wait.forLogMessage(".*was successfully deployed in.*", 1))
            .withReuse(true);


        MountableFile nginxConfFile = MountableFile.forHostPath(
            Paths.get(new File("src/test/resources/data/gw/nginx/nginx.conf").getCanonicalPath()).toAbsolutePath(),
            0777);

        MountableFile mimeFile = MountableFile.forHostPath(
            Paths.get(new File("src/test/resources/data/gw/nginx/mime.types").getCanonicalPath()).toAbsolutePath(),
            0777);
        nginxContainer = new NginxContainer<>("nginx:latest")
            .withExposedPorts(443)
            .dependsOn(payaraContainer)
            .withNetwork(network)
            .withNetworkAliases("gw")
            .withCopyFileToContainer(nginxConfFile, "/etc/nginx/nginx.conf")
            .withFileSystemBind(
                Paths.get(new File("src/test/resources/data/gw/letsencrypt").getCanonicalPath()).toAbsolutePath()
                    .toString(),
                "/etc/letsencrypt")
            .withCopyFileToContainer(mimeFile, "/etc/nginx/mime.types");

        postgresContainer.start();
        payaraContainer.start();
        nginxContainer.start();

        RestAssured.baseURI = "https://localhost"
            + ":" + nginxContainer.getMappedPort(443)
            + "/api";
        RestAssured.useRelaxedHTTPSValidation();

        generateOwnerSpec();
        generateManagerSpec();
        generateAdminSpec();
    }

    @AfterAll
    static void cleanup() {
        nginxContainer.stop();
        payaraContainer.stop();
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
