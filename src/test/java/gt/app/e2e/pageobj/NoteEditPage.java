package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class NoteEditPage extends BaseLoggedInPage<UserPage> {

    @Override
    public UserPage open() {
        return new UserPage();
    }

    public LoggedInHomePage updateNote(String title, String content) {
        getTitle().setValue(title);
        getContent().setValue(content);

        getUpdateButton().pressEnter();

        return new LoggedInHomePage();
    }

    public SelenideElement getTitle() {
        return $("#title");
    }

    public SelenideElement getContent() {
        return $("#content");
    }

    public SelenideElement getUpdateButton() {
        return $("#updateNote-btn");
    }
}
