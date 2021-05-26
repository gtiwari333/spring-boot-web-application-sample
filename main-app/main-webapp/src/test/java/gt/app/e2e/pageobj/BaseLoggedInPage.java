package gt.app.e2e.pageobj;

import static com.codeborne.selenide.Selenide.$;

public abstract class BaseLoggedInPage<U extends BaseLoggedInPage> extends BasePage<BaseLoggedInPage> {

    public UserArticleListingPage openUsersArticlePage() {
        $("#user-article-link").click();
        return new UserArticleListingPage();
    }

    public NewArticlePage newArticlePage() {
        $("#new-article-link").click();
        return new NewArticlePage();
    }

    public AdminPage openAdminPage() {
        $("#admin-area-link").click();
        return new AdminPage();
    }

    public PublicPage logout() {
        $("#navbarDropdownMenuLink").click();
        $("#logout-link").click();
        return new PublicPage();
    }

}
