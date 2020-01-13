package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoggedInHomePage extends BaseLoggedInPage<LoggedInHomePage> {

    @Override
    public LoggedInHomePage open() {
        return new LoggedInHomePage();
    }


    public LoggedInHomePage postNote(String title, String content) {
        getTitle().setValue(title);
        getContent().setValue(content);

        getPostButton().pressEnter();

        return this;
    }

    public SelenideElement getTitle() {
        return $("#title");
    }

    public SelenideElement getContent() {
        return $("#content");
    }

    public SelenideElement getPostButton() {
        return $("#postNote-btn");
    }

}
