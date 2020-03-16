package gt.app.frwk;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "server.port=8081")
public abstract class BaseSeleniumTest {

    @Container
    private static KeycloakContainer keycloak = new KeycloakContainer( "quay.io/keycloak/keycloak:9.0.0")
        .withRealmImportFile("keycloak/keycloak-export.json");

    @BeforeAll
    public static void init() {
        Configuration.headless = false;
        Configuration.browser = Browsers.FIREFOX;


        Configuration.baseUrl = "http://localhost:8081"; //same as server port

        keycloak.start();

        System.setProperty("KEYCLOAK_PORT", Integer.toString(keycloak.getHttpPort()));
    }

    @AfterEach
    public void resetPage() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }
}
