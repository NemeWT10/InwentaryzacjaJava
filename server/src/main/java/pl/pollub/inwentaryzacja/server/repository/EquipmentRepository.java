package pl.pollub.inwentaryzacja.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.inwentaryzacja.server.model.Equipment;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByWorkstationId(Long workstationId);

    List<Equipment> findByMicrocontrollerContainingIgnoreCase(String microcontroller);
}
