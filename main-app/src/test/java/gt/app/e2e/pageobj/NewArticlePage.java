package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class NewArticlePage extends BaseLoggedInPage<NewArticlePage> {

    @Override
    public NewArticlePage open() {
        return new NewArticlePage();
    }

    public LoggedInHomePage postArticle(String title, String content, String... files) {
        getTitle().setValue(title);
        getContent().setValue(content);

        if (files.length > 0) {
            getFileUploadBtn().uploadFromClasspath(files);
        }

        getPostButton().pressEnter();

        return new LoggedInHomePage();
    }

    public SelenideElement getFileUploadBtn() {
        return $("#fileUploadBtn");
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


}
