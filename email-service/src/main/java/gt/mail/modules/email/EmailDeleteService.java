package gt.mail.modules.email;

import gt.mail.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
class EmailDeleteService {

    final QueuedEmailRepository queuedEmailRepository;
    final AppProperties appProperties;

    public void deleteSentEmail(QueuedEmail email) {
        queuedEmailRepository.deleteById(email.getId());

        for (var file : email.getAttachments()) {
            try {
                deleteEmailAttachment(file.getSavedFileName());
            } catch (IOException e) {
                log.warn("Couldn't delete file " + file.getSavedFileName(), e);
            }
        }
    }

    public List<QueuedEmail> getAllSentQueuedEmailsBefore(Instant instant) {
        return queuedEmailRepository.getAllSentQueuedEmailsBefore(instant);
    }

    public void deleteEmailAttachment(String savedFileName) throws IOException {
        File file = new File(appProperties.getFileStorage().getTempFolder() + File.separator + "emails", savedFileName);
        if (file.exists()) {
            Files.delete(file.toPath());
        }

    }
}
