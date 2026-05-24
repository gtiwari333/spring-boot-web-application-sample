package gt.trend.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrendControllerIT {

    @Test
    void contextLoadsWithJmsListeners(@Autowired JmsListenerEndpointRegistry registry) {
        assertThat(registry.getListenerContainers()).isNotEmpty();
    }
}
