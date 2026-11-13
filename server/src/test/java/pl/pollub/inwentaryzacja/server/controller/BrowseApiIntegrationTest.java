package pl.pollub.inwentaryzacja.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.inwentaryzacja.server.model.Equipment;
import pl.pollub.inwentaryzacja.server.model.EquipmentStatus;
import pl.pollub.inwentaryzacja.server.model.EquipmentType;
import pl.pollub.inwentaryzacja.server.model.Role;
import pl.pollub.inwentaryzacja.server.model.Room;
import pl.pollub.inwentaryzacja.server.model.User;
import pl.pollub.inwentaryzacja.server.model.Workstation;
import pl.pollub.inwentaryzacja.server.repository.EquipmentRepository;
import pl.pollub.inwentaryzacja.server.repository.RoomRepository;
import pl.pollub.inwentaryzacja.server.repository.UserRepository;
import pl.pollub.inwentaryzacja.server.repository.WorkstationRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Sprawdza format JSON z docs/model-i-api.md, sekcja „Przeglądanie”. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BrowseApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private WorkstationRepository workstationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserRepository userRepository;

    private Room room;
    private Workstation workstation;

    @BeforeEach
    void setUp() {
        room = roomRepository.save(new Room(null, "E203", "Pracownia systemów wbudowanych"));
        Room otherRoom = roomRepository.save(new Room(null, "E204", null));
        workstation = workstationRepository.save(new Workstation(null, room, "1", "Brak zasilacza"));
        Workstation otherWorkstation = workstationRepository.save(new Workstation(null, otherRoom, "1", null));
        equipmentRepository.save(new Equipment(null, workstation, EquipmentType.BOARD, "NUCLEO-L476RG",
                "STM32L476RG", EquipmentStatus.WORKING));
        equipmentRepository.save(new Equipment(null, workstation, EquipmentType.CABLE, "USB mini-B", null,
                EquipmentStatus.WORKING));
        equipmentRepository.save(new Equipment(null, otherWorkstation, EquipmentType.BOARD, "Arduino Uno",
                "ATmega328P", EquipmentStatus.BROKEN));
    }

    @Test
    void roomsAreReturnedAsJsonArray() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].number").value("E203"))
                .andExpect(jsonPath("$[0].description").value("Pracownia systemów wbudowanych"));
    }

    @Test
    void workstationsInRoomContainRoomIdInsteadOfNestedRoom() throws Exception {
        mockMvc.perform(get("/api/workstations").param("roomId", room.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(workstation.getId()))
                .andExpect(jsonPath("$[0].roomId").value(room.getId()))
                .andExpect(jsonPath("$[0].notes").value("Brak zasilacza"))
                .andExpect(jsonPath("$[0].room").doesNotExist());
    }

    @Test
    void equipmentOnWorkstationContainsWorkstationId() throws Exception {
        mockMvc.perform(get("/api/equipment").param("workstationId", workstation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].workstationId").value(workstation.getId()))
                .andExpect(jsonPath("$[0].workstation").doesNotExist());
    }

    @Test
    void equipmentSearchByMicrocontrollerIgnoresCase() throws Exception {
        mockMvc.perform(get("/api/equipment").param("microcontroller", "stm32"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("NUCLEO-L476RG"))
                .andExpect(jsonPath("$[0].type").value("BOARD"))
                .andExpect(jsonPath("$[0].status").value("WORKING"));
    }

    @Test
    void usersAreReturnedWithoutPasswordHash() throws Exception {
        userRepository.save(new User(null, "student", "$2a$10$tajnyhash", Role.STUDENT));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login").value("student"))
                .andExpect(jsonPath("$[0].role").value("STUDENT"))
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
    }
}
