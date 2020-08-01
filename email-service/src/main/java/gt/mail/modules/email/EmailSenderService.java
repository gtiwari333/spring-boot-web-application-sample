package gt.mail.modules.email;

import gt.mail.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class EmailSenderService {

    private final QueuedEmailRepository queuedEmailRepository;
    private final EmailService queuedEmailService;

    void sendEmails(QueuedEmail.Status status, int retryCountLessThan, int pageSize) {

        int pageNumber = 0;
        Page<QueuedEmail> page = queuedEmailRepository.findAllByStatusAndRetryCountLessThan(status, retryCountLessThan, PageRequest.of(pageNumber, pageSize));
        int totalEmailsSent = 0;
        int totalEmailsFailed = 0;

        while (page.hasNext()) {

            log.info("Trying to send {} emails with  status {} and retryCountLessThan {}", page.getSize(), status, retryCountLessThan);

            Pair<Integer, Integer> emailResultPair = sendEmails(page.getContent());
            totalEmailsSent += emailResultPair.getFirst();
            totalEmailsFailed += emailResultPair.getSecond();

            page = queuedEmailRepository.findAllByStatusAndRetryCountLessThan(status, retryCountLessThan, PageRequest.of(++pageNumber, pageSize));
        }

        Pair<Integer, Integer> emailResultPair = sendEmails(page.getContent());
        totalEmailsSent += emailResultPair.getFirst();
        totalEmailsFailed += emailResultPair.getSecond();

        log.info("Total emails Sent: {} Total Emails Failed: {}", totalEmailsSent, totalEmailsFailed);
    }

    private Pair<Integer, Integer> sendEmails(List<QueuedEmail> emails) {

        int totalEmailsSent = 0;
        int totalEmailsFailed = 0;

        for (QueuedEmail email : emails) {

            if (email.getStatus() == QueuedEmail.Status.SENT) {
                continue;
            }

            try {
                queuedEmailService.deQueue(email);
                email.setStatus(QueuedEmail.Status.SENT);
                email.setSentDate(Instant.now());
                totalEmailsSent++;
            } catch (Exception e) {
                email.setStatus(QueuedEmail.Status.FAILED);
                email.setRetryCount(email.getRetryCount() + 1);
                email.setLastTryDate(Instant.now());
                email.setLastFailReason(Utils.getStackTrace(e));

                totalEmailsFailed++;

                log.error("Failed to send queued email", e);
            }

            queuedEmailRepository.save(email);
        }

        return Pair.of(totalEmailsSent, totalEmailsFailed);
    }


}
