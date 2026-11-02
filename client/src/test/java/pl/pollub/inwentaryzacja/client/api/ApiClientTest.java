package pl.pollub.inwentaryzacja.client.api;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.pollub.inwentaryzacja.client.model.Role;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

// test integracyjny: klient łączy się z prawdziwym serwerem HTTP udającym backend
class ApiClientTest {

    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void givenCorrectCredentials_whenLogin_thenTokenAndRoleReturned() throws IOException {
        ApiClient apiClient = startServerAndClient(200, "{\"token\":\"abc123\",\"role\":\"LECTURER\"}");

        LoginResponse response = apiClient.login("kowalski", "haslo");

        assertEquals(Role.LECTURER, response.role());
        assertEquals("abc123", apiClient.getToken());
    }

    @Test
    void givenWrongCredentials_whenLogin_thenThrowApiException() throws IOException {
        ApiClient apiClient = startServerAndClient(401, "");

        ApiException exception = assertThrows(ApiException.class, () -> apiClient.login("kowalski", "zle"));

        assertEquals("Błędny login lub hasło", exception.getMessage());
        assertNull(apiClient.getToken());
    }

    @Test
    void givenNoServer_whenLogin_thenThrowApiException() throws IOException {
        ApiClient apiClient = startServerAndClient(200, "");
        server.stop(0);

        ApiException exception = assertThrows(ApiException.class, () -> apiClient.login("kowalski", "haslo"));

        assertEquals("Brak połączenia z serwerem", exception.getMessage());
    }

    private ApiClient startServerAndClient(int status, String responseBody) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/auth/login", exchange -> {
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return new ApiClient("http://localhost:" + server.getAddress().getPort());
    }
}
