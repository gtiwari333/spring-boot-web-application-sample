package gt.app.modules.file;

import gt.app.domain.ReceivedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface ReceivedFileRepository extends JpaRepository<ReceivedFile, UUID> {
}
