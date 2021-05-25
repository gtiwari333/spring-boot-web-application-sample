package gt.app

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

import static java.lang.System.setProperty

abstract class BaseIntegrationSpec extends Specification {

    /*

Started by Docker TestContainer in withTestContainer profile
- ActiveMQ Artemis
- Keycloak

Embedded Apps - started in dev profile
- H2

 */

    static {
        def activeMQ = new GenericContainer<>("vromero/activemq-artemis:2.16.0")
        activeMQ.setEnv(List.of("ARTEMIS_USERNAME=admin", "ARTEMIS_PASSWORD=admin"))

        activeMQ.start() //using default ports

        def kc = new KeycloakContainer("quay.io/keycloak/keycloak:13.0.0").withRealmImportFile("keycloak/keycloak-export.json")
        kc.start()

        setProperty("KEYCLOAK_PORT", Integer.toString(kc.getHttpPort()))
        setProperty("ACTIVEMQ_ARTEMIS_HOST", activeMQ.getHost())
        setProperty("ACTIVEMQ_ARTEMIS_PORT", Integer.toString(activeMQ.getMappedPort(61616)))

    }
}
