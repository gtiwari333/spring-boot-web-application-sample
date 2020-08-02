package gt.app.modules.subscription;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class SubscriptionDTO {
    private Long id;
    private UUID subscriberId;
    private Long topicId;
    private Instant startedDate;
    private String topicName;

    public SubscriptionDTO(Long id, UUID subscriberId, Long topicId, Instant startedDate, String topicName) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.topicId = topicId;
        this.startedDate = startedDate;
        this.topicName = topicName;
    }

}
