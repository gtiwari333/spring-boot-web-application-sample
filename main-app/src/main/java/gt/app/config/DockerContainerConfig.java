package gt.app.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;

import static java.lang.System.setProperty;

@Profile("withTestContainer")
@Configuration
@Slf4j
public class DockerContainerConfig {

    /*

    Started by Docker TestContainer in withTestContainer profile
    - ElasticSearch
    - ActiveMQ Artemis
    - Keycloak

    Embedded Apps - started in dev profile
    - H2

     */


    static {
        String userPwd = "admin";//use same for all

        var es = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.8.0");
        es.start();

        var mysql = new MySQLContainer<>().withDatabaseName("seedapp").withUsername(userPwd).withPassword(userPwd);
        mysql.start();

        var activeMQ = new GenericContainer<>("vromero/activemq-artemis");
        activeMQ.setEnv(List.of("ARTEMIS_USERNAME=admin", "ARTEMIS_PASSWORD=admin"));
        activeMQ.start(); //using default ports

        var kc = new KeycloakContainer("quay.io/keycloak/keycloak:11.0.0").withRealmImportFile("keycloak/keycloak-export.json");
        kc.start();

        setProperty("ELASTICSEARCH_HOSTADDR", es.getHttpHostAddress());
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

    }

}
