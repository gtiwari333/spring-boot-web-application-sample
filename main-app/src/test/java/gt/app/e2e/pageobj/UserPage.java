package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class UserPage extends BaseLoggedInPage<UserPage> {

    @Override
    public UserPage open() {
        return new UserPage();
    }

    public LoggedInHomePage postArticle(String title, String content) {
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
        return $("#postArticle-btn");
    }

    public ArticleEditPage editArticle(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[6]/span/a").click();
        return new ArticleEditPage();
    }

    public PublicPage deletePage(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[7]/span/a").click();
        return new PublicPage();
    }

}
