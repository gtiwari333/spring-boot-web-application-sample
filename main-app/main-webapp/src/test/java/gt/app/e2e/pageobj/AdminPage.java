package gt.app.e2e.pageobj;

public class AdminPage extends BaseLoggedInPage<UserArticleListingPage> {

    @Override
    public AdminPage open() {
        return new AdminPage();
    }

    //TODO:
    //click review
    //open review page
    //accept/reject
    //accepted should display back in public page
}
