package pl.pollub.inwentaryzacja.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.inwentaryzacja.server.model.Workstation;

import java.util.List;

public interface WorkstationRepository extends JpaRepository<Workstation, Long> {

    List<Workstation> findByRoomId(Long roomId);
}
