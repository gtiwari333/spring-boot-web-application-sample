package gt.analytics.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

import static gt.common.config.CommonKafkaTopics.*;
import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@RequiredArgsConstructor
@EnableKafkaStreams
public class KafkaConfig {

    final KafkaProperties kafkaProperties;

    final Environment environment;

    short replicationFactor = 1;

    @Bean
    KafkaAdmin.NewTopics articlePublished() {
        return new KafkaAdmin.NewTopics(
            new NewTopic(ARTICLE_PUBLISHED_TOPIC, 2, replicationFactor),
            new NewTopic(ARTICLE_REJECTED_TOPIC, 2, replicationFactor),
            new NewTopic(ARTICLE_READ_TOPIC, 2, replicationFactor),
            new NewTopic(PAGE_VIEW, 2, replicationFactor),
            new NewTopic("test-word-count", 2, replicationFactor),
            new NewTopic("test-word-count-output-topic", 2, replicationFactor)
        );
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        return new KafkaStreamsConfiguration(Map.of(
            APPLICATION_ID_CONFIG, environment.getProperty("spring.application.name"),
            BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
            DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName(),
            DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()
        ));
    }

}
