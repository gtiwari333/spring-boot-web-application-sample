package gt.app.frwk;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public abstract class BaseIntegrationTest {

    static {
        var es = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.7.1");
        es.start();


        var kc = new KeycloakContainer("quay.io/keycloak/keycloak:11.0.0").withRealmImportFile("keycloak/keycloak-export.json");
        kc.start();

        System.setProperty("ELASTICSEARCH_HOSTADDR", es.getHttpHostAddress());
        System.setProperty("KEYCLOAK_PORT", Integer.toString(kc.getHttpPort()));
    }
}
