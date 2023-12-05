package gt.app.e2e.pageobj;

import com.codeborne.selenide.SelenideElement;

import java.io.File;
import java.util.List;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class UserArticleListingPage extends BaseLoggedInPage<UserArticleListingPage> {

    @Override
    public UserArticleListingPage open() {
        return new UserArticleListingPage();
    }

    public List<File> downloadFiles(int row) {
        return $$x(".//table/tbody/tr[" + row + "]/td[4]/a").asFixedIterable()
            .stream()
            .map(SelenideElement::download)
            .toList();
    }


    public ArticleEditPage editArticle(int row) {
        ///html/body/div[2]/div[1]/
        $x(".//table/tbody/tr[" + row + "]/td[6]/a").click();
        return new ArticleEditPage();
    }

    public UserArticleListingPage deletePage(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[7]/a").click();
        return new UserArticleListingPage();
    }

}
