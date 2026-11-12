package pl.pollub.inwentaryzacja.client.api;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final int HTTP_OK = 200;

    private final String baseUrl;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private final ObjectMapper jsonMapper = new JsonMapper();
    private String token;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public LoginResponse login(String login, String password) {
        String body = jsonMapper.writeValueAsString(new LoginRequest(login, password));
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = send(request);
        if (response.statusCode() != HTTP_OK) {
            throw new ApiException("Błędny login lub hasło");
        }
        LoginResponse loginResponse = jsonMapper.readValue(response.body(), LoginResponse.class);
        token = loginResponse.token();
        return loginResponse;
    }

    public <T> T get(String path, Class<T> responseType) {
        HttpResponse<String> response = sendAuthorized(request(path).GET());
        return jsonMapper.readValue(response.body(), responseType);
    }

    public void post(String path, Object body) {
        sendAuthorized(request(path).POST(jsonBody(body)));
    }

    public void put(String path, Object body) {
        sendAuthorized(request(path).PUT(jsonBody(body)));
    }

    public void delete(String path) {
        sendAuthorized(request(path).DELETE());
    }

    public String getToken() {
        return token;
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json");
    }

    private HttpRequest.BodyPublisher jsonBody(Object body) {
        return HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body));
    }

    private HttpResponse<String> sendAuthorized(HttpRequest.Builder request) {
        HttpResponse<String> response = send(request.build());
        if (!isSuccess(response.statusCode())) {
            throw new ApiException("Błąd serwera, kod: " + response.statusCode());
        }
        return response;
    }

    private static boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ApiException("Brak połączenia z serwerem");
        }
    }
}
