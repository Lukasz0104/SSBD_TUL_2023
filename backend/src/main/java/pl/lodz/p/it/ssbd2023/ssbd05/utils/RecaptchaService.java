package pl.lodz.p.it.ssbd2023.ssbd05.utils;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
public class RecaptchaService {

    @Inject
    private Properties properties;

    public boolean verifyCode(String code) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%1$s&response=%2$s",
                properties.getRecaptchaSecret(), code)))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());

            return jsonObject.getBoolean("success");
        } catch (Exception e) {
            return false;
        }
    }
}
