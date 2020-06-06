package gt.app.frwk;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class BaseSeleniumTest {

    @LocalServerPort
    int localServerPort = -1;

    @Container
    protected static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:10.0.2")
        .withRealmImportFile("keycloak/keycloak-export.json");

    @BeforeAll
    public static void init() {
        Configuration.headless = false;
        Configuration.browser = Browsers.FIREFOX;


        keycloak.start();

        System.setProperty("KEYCLOAK_PORT", Integer.toString(keycloak.getHttpPort()));
    }

    @BeforeEach
    void beforeEach() {
        Configuration.baseUrl = "http://localhost:" + localServerPort; //same as server port
    }

    @AfterEach
    public void resetPage() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }
}
