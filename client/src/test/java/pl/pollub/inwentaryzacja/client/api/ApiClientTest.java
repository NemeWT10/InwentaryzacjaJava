package pl.pollub.inwentaryzacja.client.api;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pl.pollub.inwentaryzacja.client.model.Equipment;
import pl.pollub.inwentaryzacja.client.model.EquipmentStatus;
import pl.pollub.inwentaryzacja.client.model.EquipmentType;
import pl.pollub.inwentaryzacja.client.model.Role;
import pl.pollub.inwentaryzacja.client.model.Room;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

// test integracyjny: klient łączy się z prawdziwym serwerem HTTP udającym backend
class ApiClientTest {

    private HttpServer server;
    // zapis w wątku atrapy, odczyt w teście
    private volatile String receivedAuthorization;
    private volatile String receivedBody;

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

    @Test
    void givenLoggedInUser_whenGet_thenTokenSentAndListRead() throws IOException {
        ApiClient apiClient = startServerAndClient(200, "{\"token\":\"abc123\",\"role\":\"STUDENT\"}");
        respond("/api/rooms", 200, "[{\"id\":1,\"number\":\"E203\",\"description\":null}]");
        apiClient.login("kowalski", "haslo");

        Room[] rooms = apiClient.get("/api/rooms", Room[].class);

        assertEquals("Bearer abc123", receivedAuthorization);
        assertEquals("E203", rooms[0].number());
    }

    @Test
    void givenServerRefuses_whenDelete_thenMessageFromServer() throws IOException {
        ApiClient apiClient = startServerAndClient();
        respond("/api/rooms/1", 409, "{\"status\":409,\"detail\":\"Sala ma stanowiska, nie można jej usunąć\"}");

        ApiException exception = assertThrows(ApiException.class, () -> apiClient.delete("/api/rooms/1"));

        assertEquals("Sala ma stanowiska, nie można jej usunąć", exception.getMessage());
    }

    @Test
    void givenErrorWithoutJson_whenPut_thenStatusCodeInMessage() throws IOException {
        ApiClient apiClient = startServerAndClient();
        respond("/api/rooms/1", 500, "Internal Server Error");

        ApiException exception = assertThrows(ApiException.class,
                () -> apiClient.put("/api/rooms/1", new Room(1L, "E203", null)));

        assertEquals("Błąd serwera, kod: 500", exception.getMessage());
    }

    @Test
    void givenEquipment_whenPost_thenJsonBodySent() throws IOException {
        ApiClient apiClient = startServerAndClient();
        respond("/api/equipment", 201, "");
        Equipment equipment = new Equipment(null, 1L, EquipmentType.BOARD, "NUCLEO-L476RG", "STM32L476RG",
                EquipmentStatus.WORKING);

        apiClient.post("/api/equipment", equipment);

        assertTrue(receivedBody.contains("\"name\":\"NUCLEO-L476RG\""));
        assertTrue(receivedBody.contains("\"status\":\"WORKING\""));
    }

    private ApiClient startServerAndClient(int status, String responseBody) throws IOException {
        ApiClient apiClient = startServerAndClient();
        respond("/api/auth/login", status, responseBody);
        return apiClient;
    }

    private ApiClient startServerAndClient() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.start();
        return new ApiClient("http://localhost:" + server.getAddress().getPort());
    }

    // odpowiedź atrapy pod danym adresem; zapamiętuje nagłówek Authorization i treść żądania
    private void respond(String path, int status, String responseBody) {
        server.createContext(path, exchange -> {
            receivedAuthorization = exchange.getRequestHeaders().getFirst("Authorization");
            receivedBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
    }
}
