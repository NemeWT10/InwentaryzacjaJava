package pl.pollub.inwentaryzacja.server.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Zarządzanie kontami przez administratora i logowanie na utworzone konto. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminCreatesAccountThatCanLogIn() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(user("nowak", "nowak123", "STUDENT")).with(asAdmin()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("nowak"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"nowak\", \"password\": \"nowak123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void adminChangesRoleWithoutChangingPassword() throws Exception {
        String created = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(user("zielinski", "zielinski1", "STUDENT")).with(asAdmin()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");

        mockMvc.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"zielinski\", \"role\": \"LECTURER\"}").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("LECTURER"));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"zielinski\", \"password\": \"zielinski1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("LECTURER"));
    }

    @Test
    void takenLoginReturns409() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(user("student", "student123", "STUDENT")).with(asAdmin()))
                .andExpect(status().isConflict());
    }

    @Test
    void onlyAdminManagesAccounts() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(user("intruz", "intruz123", "ADMIN"))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTURER"))))
                .andExpect(status().isForbidden());
    }

    private static String user(String login, String password, String role) {
        return """
                {"login": "%s", "password": "%s", "role": "%s"}
                """.formatted(login, password, role);
    }

    private static RequestPostProcessor asAdmin() {
        return jwt().jwt(token -> token.subject("admin")).authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
