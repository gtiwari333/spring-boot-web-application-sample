package gt.app.e2e.pageobj;

import static com.codeborne.selenide.Selenide.$;

public abstract class BaseLoggedInPage<U extends BaseLoggedInPage> extends BasePage<BaseLoggedInPage> {

    public UserPage openUserPage() {
        $("#user-note-link").click();
        return new UserPage();
    }

    public PublicPage logout() {
        $("#logout-link").click();
        return new PublicPage();
    }

}
