package gt.app;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.domain.User;
import gt.app.modules.article.ArticleService;
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

import static gtapp.jooq.Tables.ARTICLE;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DataCreator {

    final UserService userService;
    final ArticleService articleService;
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

        dsl.insertInto(ARTICLE)
            .setNull(ARTICLE.ID)
            .set(ARTICLE.CONTENT, "DSL Content Flagged ")
            .set(ARTICLE.CREATED_BY_USER_ID, user1.getId().toString())
            .set(ARTICLE.TITLE, "DSL Title Flagged")
            .set(ARTICLE.STATUS, ArticleStatus.FLAGGED.name())
            .set(ARTICLE.CREATED_DATE, Timestamp.from(Instant.now()))
            .execute();

        dsl.insertInto(ARTICLE)
            .setNull(ARTICLE.ID)
            .set(ARTICLE.CONTENT, "DSL Content Blocked... ")
            .set(ARTICLE.CREATED_BY_USER_ID, user1.getId().toString())
            .set(ARTICLE.TITLE, "DSL Title Blocked")
            .set(ARTICLE.STATUS, ArticleStatus.BLOCKED.name())
            .set(ARTICLE.CREATED_DATE, Timestamp.from(Instant.now()))
            .execute();


        createArticle(adminUser, "Admin's First Article", "Content1 Admin");
        createArticle(adminUser, "Admin's Second Article", "Content2 Admin");
        createArticle(user1, "User1 Article", "Content User 1");
        createArticle(user2, "User2 Article", "Content User 2");

        articleService.testCountStatuses();

    }

    void createArticle(User user, String title, String content) {
        var n = new Article();
        n.setCreatedByUser(user);
        n.setTitle(title);
        n.setContent(content);

        articleService.save(n);
    }


}
