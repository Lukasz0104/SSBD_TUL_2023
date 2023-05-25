package pl.lodz.p.it.ssbd2023.ssbd05.utils;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import org.json.JSONObject;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApplicationScoped
@Interceptors(LoggerInterceptor.class)
public class RecaptchaService {

    @Inject
    private AppProperties appProperties;

    public boolean verifyCode(String code) {

        String url = new StringBuilder()
            .append(appProperties.getCaptchaVerifyUrl())
            .append("?secret=")
            .append(appProperties.getRecaptchaSecret())
            .append("&response=")
            .append(code)
            .toString();

        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(url))
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
