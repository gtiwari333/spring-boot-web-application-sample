package gt.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static java.lang.System.setProperty;

@Configuration
@Slf4j
public class TestContainerConfig {

    /*

    Started by Docker TestContainer in withTestContainer profile
    - ActiveMQ Artemis

    Embedded Apps - started in dev profile
    - H2

     */

    static {
        log.info("Starting docker containers using TestContainers");

        var activeMQ = new GenericContainer<>("jhatdv/activemq-artemis:2.19.1-alpine");
        activeMQ.withExposedPorts(61616);
        activeMQ.setEnv(List.of("ARTEMIS_USERNAME=admin", "ARTEMIS_PASSWORD=admin"));

        activeMQ.start(); //using default ports

        setProperty("ACTIVEMQ_ARTEMIS_HOST", activeMQ.getHost());
        setProperty("ACTIVEMQ_ARTEMIS_PORT", Integer.toString(activeMQ.getMappedPort(61616)));

        log.info("Started docker containers using TestContainers");

    }
}
