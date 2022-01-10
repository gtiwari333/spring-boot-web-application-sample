package gt.analytics.module;

import gt.common.config.CommonKafkaTopics;
import gt.common.config.PageView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamFunctionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewStatProcessor {

    //this was required to create object of  KafkaStreamsBinderConfigurationProperties


    @KafkaListener(topics = CommonKafkaTopics.PAGE_VIEW, clientIdPrefix = "analytics", groupId = "statProcessorApp")
    public void receivePageView(ConsumerRecord<byte[], byte[]> msg) {
        log.info(new String(msg.key()) + " " + msg.timestamp() + " " + msg.offset() + " " + msg.partition() + " " + new String(msg.value()));
    }

    @Bean
    public Consumer<PageView> printer1() {
        return pageView -> log.info(pageView.agent());
    }

    @Bean
    public Consumer<KStream<String, PageView>> printer() {
        return input -> input.foreach((k, v) -> {
            log.info(k + " " + v);
        });
    }

    @Bean
    public Function<KStream<String, PageView>, KStream<String, String>> getUri() {
        return input ->
            input.map((key, pageView) ->
                new KeyValue<>(key, pageView.uri())
            );
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>> upperCase() {
        return input ->
            input.map((key, uri) -> {
                    System.out.println("" + uri);
                    return new KeyValue<>(key, uri.toUpperCase());
                }
            );
    }

    static class TestProducer {

        private AtomicBoolean semaphore = new AtomicBoolean(true);

        @Bean
        public Supplier<String> sendTestData() {
            return () -> this.semaphore.getAndSet(!this.semaphore.get()) ? "foo" : "bar";
        }
    }
}
