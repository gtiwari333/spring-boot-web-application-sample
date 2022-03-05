package gt.analytics.module;

import gt.common.config.PageView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewStatProcessor {

    @Bean
    public Function<KStream<String, PageView>, KStream<String, String>> getUri() {
        return input ->
            input
                .map((key, pageView) -> {
                    log.info("uri ## " + pageView.uri());
                    return new KeyValue<>(key, pageView.uri());
                });
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> upperCase() {
        return input ->
            input.map((key, uri) -> {
                    log.info("uppercase ## " + uri);
                    return new KeyValue<>(key, uri.toUpperCase());
                }
            );
    }

    @KafkaListener(topics = "page-uri-upper", clientIdPrefix = "analytics", groupId = "statProcessorApp")
    public void receivePageView(ConsumerRecord<String, String> msg) {
        log.info("receivePageView ## " + new String(msg.key()) + " " + msg.timestamp() + " " + msg.offset() + " " + msg.partition() + " " + new String(msg.value()));
    }

}
