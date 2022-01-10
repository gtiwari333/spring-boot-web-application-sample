package gt.app.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static gt.common.config.CommonKafkaTopics.*;

@Configuration
public class KafkaConfig {

    short replicationFactor = 1;

    @Bean
    NewTopic articlePublished() {
        return new NewTopic(ARTICLE_PUBLISHED_TOPIC, 2, replicationFactor);
    }

    @Bean
    NewTopic articleRejected() {
        return new NewTopic(ARTICLE_REJECTED_TOPIC, 2, replicationFactor);
    }

    @Bean
    NewTopic articleRead() {
        return new NewTopic(ARTICLE_READ_TOPIC, 2, replicationFactor);
    }

    @Bean
    NewTopic pageView() {
        return new NewTopic(PAGE_VIEW, 2, replicationFactor);
    }
}
