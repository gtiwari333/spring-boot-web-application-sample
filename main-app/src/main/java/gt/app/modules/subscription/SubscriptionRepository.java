package gt.app.modules.subscription;

import gt.app.domain.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("select new gt.app.modules.subscription.SubscriptionDTO(sn.id, sn.subscriber.id, sn.topic.id, sn.startedDate, sn.topic.name  )" +
        " from  Subscription sn  where sn.subscriber.id = :id")
    Page<SubscriptionDTO> getAllBySubscriberId(@Param("id") UUID subscriberId, Pageable pageable);

    boolean existsBySubscriberIdAndTopicId(UUID subscriberId, Long topic);


    @Transactional
    @Modifying
    void deleteBySubscriberIdAndTopicId(UUID subscriberId, Long topic);

}
