package gt.app;

import gt.app.domain.Note;
import gt.app.domain.NoteStatus;
import gt.app.domain.User;
import gt.app.modules.note.NoteService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

import static gtapp.jooq.Tables.NOTE;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataCreator {

    final UserService userService;
    final NoteService noteService;
    final DSLContext dsl;


    @EventListener
    public void ctxRefreshed(ContextRefreshedEvent evt) {

        log.info("Context Refreshed !!, Initializing Data... ");

        //ID and login are linked with the keycloak export json

        User adminUser = new User("a621ac4c-6172-4103-9050-b27c053b11eb", "system", "System", "Tiwari");
        userService.save(adminUser);

        User user1 = new User("d1460f56-7f7e-43e1-8396-bddf39dba08f", "user1", "Ganesh", "Tiwari");
        userService.save(user1);


        User user2 = new User("fa6820a5-cf39-4cbf-9e50-89cc832bebee", "user2", "Jyoti", "Kattel");
        userService.save(user2);

        dsl.insertInto(NOTE)
            .setNull(NOTE.ID)
            .set(NOTE.CONTENT, "DSL Content Flagged ")
            .set(NOTE.CREATED_BY_USER_ID, user1.getId().toString())
            .set(NOTE.TITLE, "DSL Title Flagged")
            .set(NOTE.STATUS, NoteStatus.FLAGGED.name())
            .set(NOTE.CREATED_DATE, Timestamp.from(Instant.now()))
            .execute();

        dsl.insertInto(NOTE)
            .setNull(NOTE.ID)
            .set(NOTE.CONTENT, "DSL Content Blocked... ")
            .set(NOTE.CREATED_BY_USER_ID, user1.getId().toString())
            .set(NOTE.TITLE, "DSL Title Blocked")
            .set(NOTE.STATUS, NoteStatus.BLOCKED.name())
            .set(NOTE.CREATED_DATE, Timestamp.from(Instant.now()))
            .execute();


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
