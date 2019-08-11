package g.t.app.repository;

import g.t.app.domain.ReceivedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceivedFileRepository extends JpaRepository<ReceivedFile, UUID> {
}
