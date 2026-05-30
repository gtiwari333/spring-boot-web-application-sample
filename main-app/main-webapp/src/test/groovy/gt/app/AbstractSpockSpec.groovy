package gt.app

import dasniko.testcontainers.keycloak.KeycloakContainer
import gt.app.frwk.TestContainerConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Testcontainers
import spock.lang.Specification


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Testcontainers
@Import(TestContainerConfig.class)
class AbstractSpockSpec extends Specification {

    // Keycloak doesn't have @ServiceConnection support, so initializing it manually
    // and registering the dynamic property
    // use the same pattern for other test containers if they do not have @ServiceConnection support
    static final KeycloakContainer keycloak =
        new KeycloakContainer("quay.io/keycloak/keycloak:26.6.1")
            .withRealmImportFile("keycloak/realm-export.json");

    static {
        keycloak.withReuse(true).start();
    }

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("keycloak.issuer-uri",
            () -> keycloak.getAuthServerUrl() + "/realms/seedapp");
    }
}
