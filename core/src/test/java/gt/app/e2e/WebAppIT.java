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

            .shouldHave(text("Content Admin 1"))
            .shouldHave(text("Content Admin 2"))
            .shouldHave(text("Content User 1"))
            .shouldHave(text("Content User 2"));

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
        testLoggedInHomePage(loggedInHomePage);

        UserPage userPage = loggedInHomePage.openUserPage();
        testUserPage(userPage);

        PublicPage publicPage = loggedInHomePage.logout();
        publicPage.body()
            .shouldHave(text("You have been signed out"));

        testAccessDenied(publicPage);
    }

    private void testLoggedInHomePage(LoggedInHomePage page) {
        page.body()
            .shouldHave(text("Logout"))
            .shouldHave(text("Post Note"))
            .shouldHave(text("User1's Notes"));

        page.postNote("New Title", "New Content");

        page.body()
            .shouldHave(text("New Title"))
            .shouldHave(text("New Content"));
    }

    private void testUserPage(UserPage page) {
        page.body()
            .shouldHave(text("Logout"))
            .shouldHave(text("Post Note"))
            .shouldHave(text("User1's Notes"))
            .shouldHave(text("Hello User1!"))
            .shouldHave(text("User1 Note"))
            //should not see other user's notes
            .shouldNotHave(text("Content Admin 1"))
            .shouldNotHave(text("Content Admin 2"))
            .shouldNotHave(text("User2 Note"))

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
