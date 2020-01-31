package gt.app;

import gt.app.config.Constants;
import gt.app.domain.Article;
import gt.app.domain.Authority;
import gt.app.domain.ArticleStatus;
import gt.app.domain.User;
import gt.app.modules.article.ArticleService;
import gt.app.modules.user.AuthorityService;
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
import java.time.LocalDate;

import static gtapp.jooq.Tables.ARTICLE;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DataCreator {

    final AuthorityService authorityService;
    final UserService userService;
    final ArticleService articleService;
    final DSLContext dsl;


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

        dsl.insertInto(ARTICLE)
            .setNull(ARTICLE.ID)
            .set(ARTICLE.CONTENT, "DSL Content Flagged ")
            .set(ARTICLE.CREATED_BY_USER_ID, user1.getId())
            .set(ARTICLE.TITLE, "DSL Title Flagged")
            .set(ARTICLE.STATUS, ArticleStatus.FLAGGED.name())
            .set(ARTICLE.CREATED_DATE, Timestamp.from(Instant.now()))
            .execute();

        dsl.insertInto(ARTICLE)
            .setNull(ARTICLE.ID)
            .set(ARTICLE.CONTENT, "DSL Content Blocked... ")
            .set(ARTICLE.CREATED_BY_USER_ID, user1.getId())
            .set(ARTICLE.TITLE, "DSL Title Blocked")
            .set(ARTICLE.STATUS, ArticleStatus.BLOCKED.name())
            .set(ARTICLE.CREATED_DATE, Timestamp.from(Instant.now()))
            .execute();


        createArticle(adminUser, "Admin's First Article", "Content1 Admin");
        createArticle(adminUser, "Admin's Second Article", "Content2 Admin");
        createArticle(user1, "User1 Article", "Content User 1");
        createArticle(user2, "User2 Article", "Content User 2");


    }

    void createArticle(User user, String title, String content) {
        var n = new Article();
        n.setCreatedByUser(user);
        n.setTitle(title);
        n.setContent(content);

        articleService.save(n);
    }


}
