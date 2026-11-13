package pl.pollub.inwentaryzacja.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pollub.inwentaryzacja.server.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByNumber(String number);
}
