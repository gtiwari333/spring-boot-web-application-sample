package gt.app.modules.file;

import gt.app.domain.ReceivedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceivedFileService {

    final ReceivedFileRepository receivedFileRepository;

    public Optional<ReceivedFile> findById(UUID id) {
        return receivedFileRepository.findById(id);
    }
}
