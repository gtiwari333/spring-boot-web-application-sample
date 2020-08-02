package gt.app.modules.subscription;

import gt.app.domain.Subscription;
import gt.app.modules.topic.TopicService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final TopicService topicService;

    public Page<SubscriptionDTO> findAllBySubscriberId(UUID subscriberId, Pageable pageable) {
        return subscriptionRepository.getAllBySubscriberId(subscriberId, pageable);
    }

    public Subscription add(UUID subscriberId, Long topicId) {
        Subscription subscription = new Subscription();
        subscription.setTopic(topicService.getReference(topicId));
        subscription.setSubscriber(userService.getReference(subscriberId));
        return subscriptionRepository.save(subscription);
    }

    public void delete(Long id) {
        subscriptionRepository.deleteById(id);
    }

    public void delete(UUID subscriberId, Long topicId) {
        subscriptionRepository.deleteBySubscriberIdAndTopicId(subscriberId, topicId);
    }

    public boolean isSubscribed(UUID subscriberId, Long topicId) {
        return subscriptionRepository.existsBySubscriberIdAndTopicId(subscriberId, topicId);
    }
}
