package gt.app.e2e.pageobj;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.$;

public class PublicPage extends BasePage<PublicPage> {

    public PublicPage open() {
        Selenide.open("/");
        return this;
    }

    public LoginPage clickLogin() {
        open();
        $("#login-link").click();

        return new LoginPage();
    }

    public void load(String url) {
        Selenide.open(url);
    }

}
