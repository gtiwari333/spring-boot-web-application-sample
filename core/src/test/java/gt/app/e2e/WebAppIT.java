package gt.app.e2e;

import gt.app.e2e.pageobj.*;
import gt.app.frwk.BaseSeleniumTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static com.codeborne.selenide.Condition.text;

@DirtiesContext
class WebAppIT extends BaseSeleniumTest {

    @Test
    void testPublicPage() {
        new PublicPage().open()
            .body()
            .shouldHave(text("Note App"))
            .shouldNotHave(text("Logout"))
            .shouldNotHave(text("Post Note"))

            .shouldHave(text("User2 Note"))
            .shouldHave(text("User1 Note"))
            .shouldHave(text("Admin's First Note"))
            .shouldHave(text("Admin's Second Note"))
            .shouldNotHave(text("DSL Title Flagged"))
            .shouldNotHave(text("DSL Title Blocked"))

            .shouldHave(text("Content1 Admin"))
            .shouldHave(text("Content2 Admin"))
            .shouldHave(text("Content User 1"))
            .shouldHave(text("Content User 2"))
            .shouldNotHave(text("DSL Content Flagged"))
            .shouldNotHave(text("DSL Content Blocked"))
        ;

        testAccessDenied(new PublicPage().open());
    }

    void testAccessDenied(PublicPage publicPage) {

        publicPage.load("/note");
        publicPage.body().shouldHave(text("Please sign in"));

        publicPage.load("/admin");
        publicPage.body().shouldHave(text("Please sign in"));
    }

    @Test
    void testLoggedInUserPage() {

        var loginPage = new LoginPage().open();

        LoggedInHomePage loggedInHomePage = loginPage.login("user1", "pass");
        testLoggedInHomePage(loggedInHomePage, "user1");

        UserPage userPage = loggedInHomePage.openUserPage();
        testUser1Page(userPage);

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
            .shouldHave(text("Logout"))
            .shouldHave(text("Post Note"))
            .shouldHave(text(username + "'s Notes"));

        page.postNote("New Title", "New Content");

        page.body()
            .shouldHave(text("New Title"))
            .shouldHave(text("New Content"));
    }

    private void testUser1Page(UserPage page) {
        page.body()
            .shouldHave(text("Logout"))
            .shouldHave(text("Post Note"))
            .shouldHave(text("User1's Notes"))
            //should not see other user's notes
            .shouldNotHave(text("Content1 Admin"))
            .shouldNotHave(text("Content2 Admin"))
            .shouldNotHave(text("User2 Note"))

            //flagged and blocked note
            .shouldNotHave(text("DSL Title Flagged"))
            .shouldNotHave(text("DSL Title Blocked"))

            .shouldNotHave(text("DSL Content Flagged"))
            .shouldNotHave(text("DSL Content Blocked"))


            //previously created note
            .shouldHave(text("New Title"))
            .shouldHave(text("New Content"));

        //user gets redirected to home page after posting
        LoggedInHomePage homePage = page.postNote("Another Title", "Another Content");

        homePage.body()
            .shouldHave(text("Another Title"))
            .shouldHave(text("Another Content"));

        //go back to user page again
        page = homePage.openUserPage();

        //edit newly created note
        NoteEditPage editPage = page.editNote(1);
        editPage.body().shouldHave(text("Update Note"));

        homePage = editPage.updateNote("Updated Title", "Updated Content");

        homePage.body()
            .shouldHave(text("Updated Title"))
            .shouldHave(text("Updated Content"))
            .shouldNotHave(text("Another Title"))
            .shouldNotHave(text("Another Content"));

        //go back to user page again
        page = homePage.openUserPage();

        //delete
        PublicPage publicPage = page.deletePage(1);
        publicPage.body()
            .shouldHave(text("Note with id"))
            .shouldHave(text("is deleted"))
            .shouldNotHave(text("Updated Title"))
            .shouldNotHave(text("Updated Content"));
    }

}
