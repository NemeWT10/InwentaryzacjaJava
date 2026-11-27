package pl.pollub.inwentaryzacja.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.inwentaryzacja.server.model.FaultReport;
import pl.pollub.inwentaryzacja.server.model.FaultReportStatus;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.FaultReportRepository;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Zgłoszenia usterek; konto „student” pochodzi z kont startowych. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FaultReportApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private WorkstationRepository workstationRepository;

    @Autowired
    private FaultReportRepository faultReportRepository;

    @Autowired
    private UserRepository userRepository;

    private Workstation workstation;

    @BeforeEach
    void setUp() {
        Room room = roomRepository.save(new Room(null, "E203", null));
        workstation = workstationRepository.save(new Workstation(null, room, "4", null));
    }

    @Test
    void studentReportsFaultAndServerFillsReporterStatusAndDate() throws Exception {
        String body = """
                {"workstationId": %d, "description": "Nie działa port USB"}
                """.formatted(workstation.getId());

        mockMvc.perform(post("/api/fault-reports").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(asStudent()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reporterLogin").value("student"))
                .andExpect(jsonPath("$.roomNumber").value("E203"))
                .andExpect(jsonPath("$.workstationNumber").value("4"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.createdAt").isString());
    }

    @Test
    void reportWithoutDescriptionIsRejectedWith400() throws Exception {
        String body = """
                {"workstationId": %d, "description": " "}
                """.formatted(workstation.getId());

        mockMvc.perform(post("/api/fault-reports").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(asStudent()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lecturerSeesOnlyOpenReportsAndCanCloseThem() throws Exception {
        FaultReport open = saveReport("Brak zasilacza", FaultReportStatus.OPEN);
        saveReport("Stara usterka", FaultReportStatus.CLOSED);

        mockMvc.perform(get("/api/fault-reports").param("status", "OPEN").with(asLecturer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Brak zasilacza"))
                .andExpect(jsonPath("$[0].reporterLogin").value("student"));

        mockMvc.perform(put("/api/fault-reports/{id}", open.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"CLOSED\"}").with(asLecturer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));

        mockMvc.perform(get("/api/fault-reports").param("status", "OPEN").with(asLecturer()))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void rolesAreCheckedForReportingAndBrowsingReports() throws Exception {
        String body = """
                {"workstationId": %d, "description": "Nie działa port USB"}
                """.formatted(workstation.getId());

        mockMvc.perform(post("/api/fault-reports").contentType(MediaType.APPLICATION_JSON).content(body)
                        .with(asLecturer()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/fault-reports").with(asStudent()))
                .andExpect(status().isForbidden());
    }

    private FaultReport saveReport(String description, FaultReportStatus status) {
        User student = userRepository.findByLogin("student").orElseThrow();
        return faultReportRepository.save(new FaultReport(null, workstation, student, description, status, null));
    }

    private static RequestPostProcessor asStudent() {
        return jwt().jwt(token -> token.subject("student")).authorities(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    private static RequestPostProcessor asLecturer() {
        return jwt().jwt(token -> token.subject("wykladowca"))
                .authorities(new SimpleGrantedAuthority("ROLE_LECTURER"));
    }
}
