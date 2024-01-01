package gt.app.frwk;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

//update selenium tests to extend this class so that we can write/test tests faster against running application
//this will not start the application in test profile, which makes it easier to write and test the tests
public abstract class StandAloneSeleniumTest {

    @BeforeAll
    public static void init() {
        Configuration.headless = false;
        Configuration.browser = Browsers.EDGE;
    }

    @BeforeEach
    void beforeEach() {
        Configuration.baseUrl = "http://localhost:8081";
    }

    @AfterEach
    public void resetPage() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }
}
