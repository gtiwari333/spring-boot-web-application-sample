package gt.app.e2e.pageobj;

import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    public LoggedInHomePage login(String username, String password) {

        $("#username").setValue(username);
        $("#password").setValue(password);
        $("#password").sendKeys(Keys.ENTER);

        return new LoggedInHomePage();
    }


    @Override
    public LoginPage open() {
        var publicPage = new PublicPage();
        publicPage.open();
        return publicPage.clickLogin();
    }
}
