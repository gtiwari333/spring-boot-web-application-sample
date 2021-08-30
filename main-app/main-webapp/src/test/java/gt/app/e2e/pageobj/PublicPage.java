package gt.app.e2e.pageobj;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.$;

public class PublicPage extends BasePage<PublicPage> {

    public PublicPage open() {
        return open("");
    }

    public PublicPage open(String urlParam) {
        Selenide.open("/" + urlParam);
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
