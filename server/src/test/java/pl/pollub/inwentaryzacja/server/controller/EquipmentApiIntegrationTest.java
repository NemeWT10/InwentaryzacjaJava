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
import pl.pollub.inwentaryzacja.server.model.Equipment;
import pl.pollub.inwentaryzacja.server.model.EquipmentStatus;
import pl.pollub.inwentaryzacja.server.model.EquipmentType;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.EquipmentRepository;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Zapis sprzętu w formacie z docs/model-i-api.md, sekcja „Zapis (wykładowca)”. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EquipmentApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private WorkstationRepository workstationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    private Workstation workstation;

    @BeforeEach
    void setUp() {
        Room room = roomRepository.save(new Room(null, "E203", null));
        workstation = workstationRepository.save(new Workstation(null, room, "1", null));
    }

    @Test
    void createdEquipmentIsReturnedWith201AndSavedInDatabase() throws Exception {
        String body = """
                {"id": null, "workstationId": %d, "type": "BOARD", "name": "NUCLEO-L476RG",
                 "microcontroller": "STM32L476RG", "status": "WORKING"}
                """.formatted(workstation.getId());

        mockMvc.perform(post("/api/equipment").contentType(MediaType.APPLICATION_JSON).content(body).with(asLecturer()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.workstationId").value(workstation.getId()))
                .andExpect(jsonPath("$.name").value("NUCLEO-L476RG"));

        assertEquals(1, equipmentRepository.findByWorkstationId(workstation.getId()).size());
    }

    @Test
    void equipmentWithoutNameIsRejectedWith400() throws Exception {
        String body = """
                {"workstationId": %d, "type": "CABLE", "name": "", "status": "WORKING"}
                """.formatted(workstation.getId());

        mockMvc.perform(post("/api/equipment").contentType(MediaType.APPLICATION_JSON).content(body).with(asLecturer()))
                .andExpect(status().isBadRequest());

        assertTrue(equipmentRepository.findAll().isEmpty());
    }

    @Test
    void updateChangesEquipmentStatus() throws Exception {
        Equipment board = equipmentRepository.save(new Equipment(null, workstation, EquipmentType.BOARD,
                "Arduino Uno", "ATmega328P", EquipmentStatus.WORKING));
        String body = """
                {"id": %d, "workstationId": %d, "type": "BOARD", "name": "Arduino Uno",
                 "microcontroller": "ATmega328P", "status": "IN_REPAIR"}
                """.formatted(board.getId(), workstation.getId());

        mockMvc.perform(put("/api/equipment/{id}", board.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(body).with(asLecturer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_REPAIR"));

        mockMvc.perform(get("/api/equipment").param("workstationId", workstation.getId().toString())
                        .with(asLecturer()))
                .andExpect(jsonPath("$[0].status").value("IN_REPAIR"));
    }

    @Test
    void deleteReturns204AndSecondDeleteReturns404() throws Exception {
        Equipment cable = equipmentRepository.save(new Equipment(null, workstation, EquipmentType.CABLE,
                "USB mini-B", null, EquipmentStatus.WORKING));

        mockMvc.perform(delete("/api/equipment/{id}", cable.getId()).with(asLecturer()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/equipment/{id}", cable.getId()).with(asLecturer()))
                .andExpect(status().isNotFound());
    }

    private static RequestPostProcessor asLecturer() {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_LECTURER"));
    }
}
