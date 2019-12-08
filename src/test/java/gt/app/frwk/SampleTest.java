package gt.app.frwk;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class SampleTest {

    @BeforeEach
    void setup() {
        Configuration.baseUrl = "https://en.wikipedia.org/wiki/Main_Page";
        Configuration.headless = true;
        Configuration.browser = Browsers.FIREFOX;
    }

    @Test
    void test() {
        open("/");

        $("#searchInput").setValue("Software");

        $("#searchButton").click();

        $("body").shouldHave(text("Computer Software"));
    }


}
