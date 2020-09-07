package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.*;

public class UserPage extends BaseLoggedInPage<UserPage> {

    @Override
    public UserPage open() {
        return new UserPage();
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

    public List<File> downloadFiles(int row) {
        return $$x(".//table/tbody/tr[" + row + "]/td[4]/span/a").stream().map(
            a -> {
                try {
                    return a.download();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException();
                }
            }
        ).collect(Collectors.toList());

    }


    public ArticleEditPage editArticle(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[6]/span/a").click();
        return new ArticleEditPage();
    }

    public UserPage deletePage(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[7]/span/a").click();
        return new UserPage();
    }

}
