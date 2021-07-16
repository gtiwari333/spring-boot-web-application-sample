package gt.app.frwk;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

import static java.lang.System.setProperty;

public abstract class BaseIntegrationTest {


    /*

    Started by Docker TestContainer in withTestContainer profile
    - ActiveMQ Artemis
    - Keycloak

    Embedded Apps - started in dev profile
    - H2

     */

    static {
        var activeMQ = new GenericContainer<>("vromero/activemq-artemis:2.16.0");
        activeMQ.setEnv(List.of("ARTEMIS_USERNAME=admin", "ARTEMIS_PASSWORD=admin"));

        activeMQ.start(); //using default ports

        var kc = new KeycloakContainer("quay.io/keycloak/keycloak:14.0.0").withRealmImportFile("keycloak/keycloak-export.json");
        kc.start();

        setProperty("KEYCLOAK_PORT", Integer.toString(kc.getHttpPort()));
        setProperty("ACTIVEMQ_ARTEMIS_HOST", activeMQ.getHost());
        setProperty("ACTIVEMQ_ARTEMIS_PORT", Integer.toString(activeMQ.getMappedPort(61616)));

    }
}
