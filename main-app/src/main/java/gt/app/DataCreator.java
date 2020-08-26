package gt.app;

import gt.app.domain.*;
import gt.app.modules.article.ArticleService;
import gt.app.modules.article.CommentService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static gtapp.jooq.Tables.G_ARTICLE;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DataCreator {

    final UserService userService;
    final ArticleService articleService;
    final CommentService commentService;
    final DSLContext dsl;


    @EventListener
    public void ctxRefreshed(ContextRefreshedEvent evt) {

        log.info("Context Refreshed !!, Initializing Data... ");

        String systemUserId = "a621ac4c-6172-4103-9050-b27c053b11eb";

        if (userService.exists(UUID.fromString(systemUserId))) {
            log.info("DB already initialized !!!");
            return;
        }

        //ID and login are linked with the keycloak export json
        User adminUser = new User(systemUserId, "system", "System", "Tiwari");
        userService.save(adminUser);

        User user1 = new User("d1460f56-7f7e-43e1-8396-bddf39dba08f", "user1", "Ganesh", "Tiwari");
        userService.save(user1);


        User user2 = new User("fa6820a5-cf39-4cbf-9e50-89cc832bebee", "user2", "Jyoti", "Kattel");
        userService.save(user2);

        dsl.insertInto(G_ARTICLE)
            .setNull(G_ARTICLE.ID)
            .set(G_ARTICLE.CONTENT, "DSL Content Flagged ")
            .set(G_ARTICLE.CREATED_BY_USER_ID, user1.getId().toString())
            .set(G_ARTICLE.TITLE, "DSL Title Flagged")
            .set(G_ARTICLE.STATUS, ArticleStatus.FLAGGED.name())
            .set(G_ARTICLE.CREATED_DATE, LocalDateTime.now())
            .execute();

        dsl.insertInto(G_ARTICLE)
            .setNull(G_ARTICLE.ID)
            .set(G_ARTICLE.CONTENT, "DSL Content Blocked... ")
            .set(G_ARTICLE.CREATED_BY_USER_ID, user1.getId().toString())
            .set(G_ARTICLE.TITLE, "DSL Title Blocked")
            .set(G_ARTICLE.STATUS, ArticleStatus.BLOCKED.name())
            .set(G_ARTICLE.CREATED_DATE, LocalDateTime.now())
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

        Comment c = new Comment();
        c.setStatus(CommentStatus.SHOWING);
        c.setContent("Test comment for " + title);
        c.setArticleId(n.getId());
        c.setCreatedByUser(user); //self
        commentService.save(c);

    }


}
