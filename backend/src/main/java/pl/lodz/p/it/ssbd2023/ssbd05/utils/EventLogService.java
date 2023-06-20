package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class EventLogService {
    private static final Jsonb jsonb = JsonbBuilder.create();

    @Inject
    private AppProperties appProperties;

    private MongoClient client;

    private MongoCollection eventCollection;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private void initConnection() {
        MongoCredential credential = MongoCredential.createCredential(
            appProperties.getEventLogUser(), "admin", appProperties.getEventLogPass().toCharArray()
        );

        ConnectionString conn = new ConnectionString(
            "mongodb://"
                + appProperties.getEventLogConnectionHostname()
                + "/" + appProperties.getEventLogConnectionPort());

        client = MongoClients.create(
            MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(conn)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build());

        eventCollection = client.getDatabase("EVENTS").getCollection("events");
    }

    public void saveEvent(String methodName, Object[] parameters, String login) {
        if (eventCollection == null) {
            initConnection();
        }

        executorService.execute(() -> eventCollection.insertOne(
            new Document("methodName", methodName)
                .append("login", login)
                .append("timestamp", LocalDateTime.now())
                .append("parameters", Arrays.stream(parameters).map(jsonb::toJson).toList())
        ));
    }
}
