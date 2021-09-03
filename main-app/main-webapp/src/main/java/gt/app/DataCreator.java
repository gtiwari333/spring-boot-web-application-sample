package gt.app;

import gt.app.config.AppProperties;
import gt.app.domain.*;
import gt.app.modules.article.ArticleService;
import gt.app.modules.article.CommentService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DataCreator {

    final UserService userService;
    final ArticleService articleService;
    final CommentService commentService;
    final AppProperties appProperties;

    @EventListener
    public void ctxRefreshed(ContextRefreshedEvent evt) {
        initData();
    }

    public void initData() {

        log.info("Context Refreshed !!, Initializing environment (db, folders etc)... ");

        File uploadFolder = new File(appProperties.getFileStorage().getUploadFolder());
        if (!uploadFolder.exists()) {
            if (uploadFolder.mkdirs() && Stream.of(ReceivedFile.FileGroup.values()).allMatch(f -> new File(uploadFolder.getAbsolutePath() + File.separator + f.path).mkdir())) {
                log.info("Upload folder created successfully");
            } else {
                log.info("Failure to create upload folder");
            }
        }

        String systemUserId = "a621ac4c-6172-4103-9050-b27c053b11eb"; //matches with keycloak user id

        if (userService.exists(UUID.fromString(systemUserId))) {
            log.info("DB already initialized !!!");
            return;
        }

        //ID and login are linked with the keycloak export json
        User adminUser = new User(systemUserId, "system", "System", "Tiwari", "system@email");
        userService.save(adminUser);

        User user1 = new User("d1460f56-7f7e-43e1-8396-bddf39dba08f", "user1", "Ganesh", "Tiwari", "user1@email");
        userService.save(user1);


        User user2 = new User("fa6820a5-cf39-4cbf-9e50-89cc832bebee", "user2", "Jyoti", "Kattel", "user2@email");
        userService.save(user2);

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
