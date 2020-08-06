package gt.app.frwk;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseSeleniumTest extends BaseIntegrationTest{

    @LocalServerPort
    int localServerPort = -1;


    @BeforeAll
    public static void init() {
        Configuration.headless = false;
        Configuration.browser = Browsers.FIREFOX;
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
