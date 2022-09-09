package gt.app.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

import static java.lang.System.setProperty;

@Profile("withTestContainer")
@Configuration
@Slf4j
public class DockerContainerConfig {

    /*

    Started by Docker TestContainer in withTestContainer profile
    - ActiveMQ Artemis
    - Keycloak

    Embedded Apps - started in dev profile
    - H2

     */


    static {
        log.info("Starting docker containers using TestContainers");

        String userPwd = "admin";//use same for all

        var mysql = new MySQLContainer<>("mysql:8.0.30").withDatabaseName("seedapp").withUsername(userPwd).withPassword(userPwd);
        mysql.start();

        var activeMQ = new GenericContainer<>("jhatdv/activemq-artemis:2.19.1-alpine");
        activeMQ.setEnv(List.of("ARTEMIS_USERNAME=admin", "ARTEMIS_PASSWORD=admin"));
        activeMQ.withExposedPorts(61616);
        activeMQ.start(); //using default ports

        var kc = new KeycloakContainer("quay.io/keycloak/keycloak:19.0.1").withRealmImportFile("keycloak/realm-export.json");
        kc.start();

        setProperty("KEYCLOAK_PORT", Integer.toString(kc.getHttpPort()));
        setProperty("ACTIVEMQ_ARTEMIS_HOST", activeMQ.getHost());
        setProperty("ACTIVEMQ_ARTEMIS_PORT", Integer.toString(activeMQ.getMappedPort(61616)));
        setProperty("ACTIVEMQ_ARTEMIS_USERNAME", userPwd);
        setProperty("ACTIVEMQ_ARTEMIS_PASSWORD", userPwd);

        setProperty("MYSQL_HOST", mysql.getHost());
        setProperty("MYSQL_PORT", Integer.toString(mysql.getMappedPort(3306)));
        setProperty("MYSQL_DB", "seedapp");
        setProperty("MYSQL_USERNAME", userPwd);
        setProperty("MYSQL_PASSWORD", userPwd);

        log.info("Started docker containers using TestContainers");
    }

}
