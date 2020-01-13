package gt.app;

import gt.app.config.Constants;
import gt.app.domain.Authority;
import gt.app.domain.Note;
import gt.app.domain.User;
import gt.app.modules.note.NoteService;
import gt.app.modules.user.AuthorityService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataCreator {

    final AuthorityService authorityService;
    final UserService userService;
    final NoteService noteService;


    @EventListener
    public void ctxRefreshed(ContextRefreshedEvent evt) {

        log.info("Context Refreshed !!, Initializing Data... ");

        Authority adminAuthority = new Authority();
        adminAuthority.setName(Constants.ROLE_ADMIN);
        authorityService.save(adminAuthority);

        Authority userAuthority = new Authority();
        userAuthority.setName(Constants.ROLE_USER);
        authorityService.save(userAuthority);

        String pwd = "$2a$10$UtqWHf0BfCr41Nsy89gj4OCiL36EbTZ8g4o/IvFN2LArruHruiRXO"; // to make it faster //value is 'pass'

        User adminUser = new User("system", LocalDate.now().minusYears(10), "System", "Tiwari", "system@email");
        adminUser.setPassword(pwd);
        adminUser.setAuthorities(authorityService.findByNameIn(Constants.ROLE_ADMIN, Constants.ROLE_USER));
        userService.save(adminUser);

        User user1 = new User("user1", LocalDate.now().minusYears(10), "Ganesh", "Tiwari", "gt@email");
        user1.setPassword(pwd);
        user1.setAuthorities(authorityService.findByNameIn(Constants.ROLE_USER));
        userService.save(user1);


        User user2 = new User("user2", LocalDate.now().minusYears(1), "Jyoti", "Kattel", "jk@email");
        user2.setPassword(pwd);
        user2.setAuthorities(authorityService.findByNameIn(Constants.ROLE_USER));
        userService.save(user2);

        createNote(adminUser, "Admin's First Note", "Content Admin 1");
        createNote(adminUser, "Admin's Second Note", "Content Admin 2");
        createNote(user1, "User1 Note", "Content User 1");
        createNote(user2, "User2 Note", "Content User 2");


    }

    void createNote(User user, String title, String content) {
        var n = new Note();
        n.setCreatedByUser(user);
        n.setTitle(title);
        n.setContent(content);

        noteService.save(n);
    }


}
