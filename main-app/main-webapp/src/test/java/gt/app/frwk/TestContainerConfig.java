package gt.app.frwk;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.activemq.ArtemisContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

    @Bean
    @ServiceConnection
    ArtemisContainer artemis() {
        // activemq-artemis has @SeriviceConnection support, so using it here.
        return new ArtemisContainer("apache/activemq-artemis:2.44.0");
    }

}
