package gt.mail.modules.email;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

interface QueuedEmailRepository extends JpaRepository<QueuedEmail, Long> {

    @EntityGraph(attributePaths = {"attachments"})
    Page<QueuedEmail> findAllByStatusAndRetryCountLessThan(QueuedEmail.Status status, int retryCount, Pageable page);

    @Query("select qe from QueuedEmail qe where qe.status = 'SENT' AND qe.sentDate < :Instant")
    @EntityGraph(attributePaths = {"attachments"})
    List<QueuedEmail> getAllSentQueuedEmailsBefore(@Param("Instant") Instant instant);


}
