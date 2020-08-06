package gt.app.frwk;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;

public abstract class BaseIntegrationTest {


    /*

    Started by Docker TestContainer in withTestContainer profile
    - ElasticSearch
    - ActiveMQ Artemis
    - Keycloak

    Embedded Apps - started in dev profile
    - H2

     */

    static {
        var es = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.8.0");
        es.start();

        var activeMQ = new GenericContainer("vromero/activemq-artemis");
        activeMQ.setEnv(List.of("ARTEMIS_USERNAME=admin", "ARTEMIS_PASSWORD=admin"));

        activeMQ.start(); //using default ports

        var kc = new KeycloakContainer("quay.io/keycloak/keycloak:11.0.0").withRealmImportFile("keycloak/keycloak-export.json");
        kc.start();

        System.setProperty("ELASTICSEARCH_HOSTADDR", es.getHttpHostAddress());
        System.setProperty("KEYCLOAK_PORT", Integer.toString(kc.getHttpPort()));
        System.setProperty("ACTIVEMQ_ARTEMIS_HOST", activeMQ.getHost());
        System.setProperty("ACTIVEMQ_ARTEMIS_PORT", Integer.toString(activeMQ.getMappedPort(61616)));

    }
}
