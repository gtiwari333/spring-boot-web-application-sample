package gt.app.e2e;

import gt.app.e2e.pageobj.*;
import gt.app.frwk.BaseSeleniumTest;
import gt.app.frwk.TestDataManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Locale;

import static com.codeborne.selenide.Condition.text;

class WebAppIT extends BaseSeleniumTest {

    @Autowired
    TestDataManager testDataManager;

    @BeforeEach
    void cleanDB(){
        testDataManager.truncateTablesAndRecreate();
    }

    @Test
    void testPublicPage(@Autowired MessageSource ms) {
        new PublicPage().open()
            .body()
            .shouldHave(text("Blog App"))
            .shouldNotHave(text("Post Article"))

            .shouldHave(text("User2 Article"))
            .shouldHave(text("User1 Article"))
            .shouldHave(text("Admin's First Article"))
            .shouldHave(text("Admin's Second Article"))
            .shouldNotHave(text("DSL Title Flagged"))
            .shouldNotHave(text("DSL Title Blocked"))

            .shouldHave(text("Content1 Admin"))
            .shouldHave(text("Content2 Admin"))
            .shouldHave(text("Content User 1"))
            .shouldHave(text("Content User 2"))
            .shouldNotHave(text("DSL Content Flagged"))
            .shouldNotHave(text("DSL Content Blocked"))
        ;

        new PublicPage().open("?lang=np")
            .body()
            .shouldHave(text(ms.getMessage("blogapp.title", new Object[]{}, Locale.forLanguageTag("np"))));

        new PublicPage().open("?lang=en")
            .body()
            .shouldHave(text(ms.getMessage("blogapp.title", new Object[]{}, Locale.forLanguageTag("en"))));

        testAccessDenied(new PublicPage().open());
    }

    void testAccessDenied(PublicPage publicPage) {

        publicPage.load("/article");
        publicPage.body().shouldHave(text("Sign in to your account"));

        publicPage.load("/admin");
        publicPage.body().shouldHave(text("Sign in to your account"));
    }

    @Test
    void testLoggedInUserPage() {

        var loginPage = new LoginPage().open();

        LoggedInHomePage loggedInHomePage = loginPage.login("user1", "pass");
        testLoggedInHomePage(loggedInHomePage, "user1");

        UserArticleListingPage userArticleListingPage = loggedInHomePage.openUsersArticlePage();
        testUser1Page(userArticleListingPage);

        PublicPage publicPage = loggedInHomePage.logout();
        publicPage.body()
            .shouldHave(text("You have been signed out"));

        testAccessDenied(publicPage);
    }

    @Test
    void testAdminPage() {

        var loginPage = new LoginPage().open();

        var adminHome = loginPage.login("system", "pass");
        testLoggedInHomePage(adminHome, "system");

        AdminPage admPage = adminHome.openAdminPage();
        testAdminPage(admPage);

        PublicPage publicPage = adminHome.logout();
        publicPage.body()
            .shouldHave(text("You have been signed out"));

        testAccessDenied(publicPage);
    }

    private void testAdminPage(AdminPage adminPage) {
        //TODO: test review page
    }

    private void testLoggedInHomePage(LoggedInHomePage page, String username) {
        //common home page for any user
        page.body()
            .shouldHave(text(username))
            .shouldHave(text("Post Article"))
            .shouldHave(text(username + "'s Articles"));

        page.postArticle("New Title", "New Content");

        page.body()
            .shouldHave(text("New Title"))
            .shouldHave(text("New Content"));
    }

    private void testUser1Page(UserArticleListingPage userArticleListingPage) {
        userArticleListingPage.body()
            .shouldHave(text("My Articles"))
            //should not see other user's articles
            .shouldNotHave(text("Content1 Admin"))
            .shouldNotHave(text("Content2 Admin"))
            .shouldNotHave(text("User2 Article"))

            //flagged and blocked article
            .shouldNotHave(text("DSL Title Flagged"))
            .shouldNotHave(text("DSL Title Blocked"))

            .shouldNotHave(text("DSL Content Flagged"))
            .shouldNotHave(text("DSL Content Blocked"))

            //previously created article
            .shouldHave(text("New Title"))
            .shouldHave(text("New Content"));


        /*
         POST article
         */
        //user gets redirected to home page after posting
        NewArticlePage newArticlePage = userArticleListingPage.newArticlePage();
        LoggedInHomePage homePage = newArticlePage.postArticle("Another Title", "Another Content");

        homePage.body()
            .shouldHave(text("Another Title"))
            .shouldHave(text("Another Content"));

        //go back to user page again
        userArticleListingPage = homePage.openUsersArticlePage();

        //edit newly created article
        ArticleEditPage editPage = userArticleListingPage.editArticle(1);
        editPage.body().shouldHave(text("Update Article"));

        homePage = editPage.updateArticle("Updated Title", "Updated Content");

        homePage.body()
            .shouldHave(text("Updated Title"))
            .shouldHave(text("Updated Content"))
            .shouldNotHave(text("Another Title"))
            .shouldNotHave(text("Another Content"));



        /*
        POST article with attachment
         */
        //user gets redirected to home page after posting
        newArticlePage = userArticleListingPage.newArticlePage();
        homePage = newArticlePage.postArticle("Title with file", "Content with file", "blob/test.txt", "blob/test2.txt");

        homePage.body()
            .shouldHave(text("Title with file"))
            .shouldHave(text("Content with file"))
            .shouldHave(text("test2.txt"))
            .shouldHave(text("test.txt"));

        //go back to user page again
        userArticleListingPage = homePage.openUsersArticlePage();

        Assertions.assertEquals(2, userArticleListingPage.downloadFiles(1).size());

        //delete
        UserArticleListingPage publicPage = userArticleListingPage
            .deletePage(1)
            .deletePage(1);
        publicPage.body()
            .shouldHave(text("Article with id"))
            .shouldHave(text("is deleted"))
            .shouldNotHave(text("Updated Title"))
            .shouldNotHave(text("Updated Content"));
    }

}
