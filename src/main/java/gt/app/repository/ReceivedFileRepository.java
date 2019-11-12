package gt.app.repository;

import gt.app.domain.ReceivedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceivedFileRepository extends JpaRepository<ReceivedFile, UUID> {
}
