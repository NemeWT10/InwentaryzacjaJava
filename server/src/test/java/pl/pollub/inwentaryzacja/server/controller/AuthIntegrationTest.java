package pl.pollub.inwentaryzacja.server.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Logowanie na kontach startowych i uprawnienia ról, z prawdziwym tokenem z /api/auth/login. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginReturnsTokenAndRole() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("wykladowca", "wykladowca123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("LECTURER"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials("wykladowca", "zlehaslo")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithForgedTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/rooms").header(HttpHeaders.AUTHORIZATION, "Bearer nie.jest.tokenem"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFromLoginAllowsBrowsing() throws Exception {
        mockMvc.perform(get("/api/rooms").header(HttpHeaders.AUTHORIZATION, bearer("student", "student123")))
                .andExpect(status().isOk());
    }

    @Test
    void studentCannotAddEquipment() throws Exception {
        mockMvc.perform(post("/api/equipment").contentType(MediaType.APPLICATION_JSON).content("{}")
                        .header(HttpHeaders.AUTHORIZATION, bearer("student", "student123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void lecturerCannotAddRoomButAdminCan() throws Exception {
        String room = """
                {"number": "E301", "description": "Nowa sala"}
                """;
        mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON).content(room)
                        .header(HttpHeaders.AUTHORIZATION, bearer("wykladowca", "wykladowca123")))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/rooms").contentType(MediaType.APPLICATION_JSON).content(room)
                        .header(HttpHeaders.AUTHORIZATION, bearer("admin", "admin123")))
                .andExpect(status().isCreated());
    }

    private String bearer(String login, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(credentials(login, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(response, "$.token");
        return "Bearer " + token;
    }

    private static String credentials(String login, String password) {
        return """
                {"login": "%s", "password": "%s"}
                """.formatted(login, password);
    }
}
