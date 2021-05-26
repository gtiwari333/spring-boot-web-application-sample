package gt.app.e2e.pageobj;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class UserArticleListingPage extends BaseLoggedInPage<UserArticleListingPage> {

    @Override
    public UserArticleListingPage open() {
        return new UserArticleListingPage();
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

    public UserArticleListingPage deletePage(int row) {
        $x(".//table/tbody/tr[" + row + "]/td[7]/span/a").click();
        return new UserArticleListingPage();
    }

}
