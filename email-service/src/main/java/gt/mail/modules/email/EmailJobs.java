package gt.mail.modules.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class EmailJobs {

    final EmailSenderService sender;
    final EmailDeleteService deleteService;

    @Scheduled(fixedDelay = 60 * 1000) //every mins
    public void sendQueuedEmails() {
        sender.sendEmails(QueuedEmail.Status.QUEUED, 0, 50);
    }

    @Scheduled(fixedDelay = 2 * 60 * 1000) // every 2 min
    public void sendFailedEmails() {
        sender.sendEmails(QueuedEmail.Status.FAILED, 5, 10);
    }


    @Scheduled(fixedDelay = 2 * 24 * 60 * 60 * 1000) //every 2 day
    public void deleteSentEmails() {

        deleteService.getAllSentQueuedEmailsBefore(Instant.now().minus(5, ChronoUnit.DAYS))
            .forEach(deleteService::deleteSentEmail);

    }


}
