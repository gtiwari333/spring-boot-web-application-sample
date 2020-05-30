package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class UserPage extends BaseLoggedInPage<UserPage> {

    @Override
    public UserPage open() {
        return new UserPage();
    }

    public LoggedInHomePage postNote(String title, String content) {
        getTitle().setValue(title);
        getContent().setValue(content);

        getPostButton().pressEnter();

        return new LoggedInHomePage();
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

    public NoteEditPage editNote(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[5]/span/a").click();
        return new NoteEditPage();
    }

    public PublicPage deletePage(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[6]/span/a").click();
        return new PublicPage();
    }

}
