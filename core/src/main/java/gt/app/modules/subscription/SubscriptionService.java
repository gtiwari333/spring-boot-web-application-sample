package gt.app.modules.subscription;

import gt.app.domain.Subscription;
import gt.app.domain.Topic;
import gt.app.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final EntityManager entityManager;

    public Page<SubscriptionDTO> findAllBySubscriberId(Long subscriberId, Pageable pageable) {
        return subscriptionRepository.getAllBySubscriberId(subscriberId, pageable);
    }

    public Subscription add(Long subscriberId, Long topicId) {
        Subscription subscription = new Subscription();
        subscription.setTopic(entityManager.getReference(Topic.class, topicId));
        subscription.setSubscriber(entityManager.getReference(User.class, subscriberId));
        return subscriptionRepository.save(subscription);
    }

    public void delete(Long id) {
        subscriptionRepository.deleteById(id);
    }

    public void delete(Long subscriberId, Long topicId) {
        subscriptionRepository.deleteBySubscriberIdAndTopicId(subscriberId, topicId);
    }

    public boolean isSubscribed(Long subscriberId, Long topicId) {
        return subscriptionRepository.existsBySubscriberIdAndTopicId(subscriberId, topicId);
    }
}
