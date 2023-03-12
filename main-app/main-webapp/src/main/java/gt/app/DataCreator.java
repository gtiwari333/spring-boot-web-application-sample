package gt.app;

import gt.app.config.AppProperties;
import gt.app.config.Constants;
import gt.app.domain.*;
import gt.app.modules.article.ArticleRepository;
import gt.app.modules.article.CommentRepository;
import gt.app.modules.user.AuthorityService;
import gt.app.modules.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.stream.Stream;

@Component
@Profile({"dev", "test"})
@RequiredArgsConstructor
@Slf4j
public class DataCreator {
    final AuthorityService authorityService;

    final UserService userService;
    final ArticleRepository articleRepository;
    final CommentRepository commentRepository;
    final AppProperties appProperties;

    public static final String USER1 = "user1";
    public static final String USER2 = "user2";
    public static final String SYSTEM_USER = "system"; //admin

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

        if (userService.existsByUsername("system")) {
            log.info("DB already initialized !!!");
            return;
        }

        Authority adminAuthority = new Authority();
        adminAuthority.setName(Constants.ROLE_ADMIN);
        authorityService.save(adminAuthority);

        Authority userAuthority = new Authority();
        userAuthority.setName(Constants.ROLE_USER);
        authorityService.save(userAuthority);

        String pwd = "$2a$10$UtqWHf0BfCr41Nsy89gj4OCiL36EbTZ8g4o/IvFN2LArruHruiRXO"; // to make it faster //value is 'pass'

        AppUser adminUser = new AppUser(SYSTEM_USER, "System", "Tiwari", "system@email");
        adminUser.setPassword(pwd);
        adminUser.setAuthorities(authorityService.findByNameIn(Constants.ROLE_ADMIN, Constants.ROLE_USER));
        userService.save(adminUser);

        AppUser user1 = new AppUser(USER1, "Ganesh", "Tiwari", "gt@email");
        user1.setPassword(pwd);
        user1.setAuthorities(authorityService.findByNameIn(Constants.ROLE_USER));
        userService.save(user1);

        AppUser user2 = new AppUser(USER2, "Jyoti", "Kattel", "jk@email");
        user2.setPassword(pwd);
        user2.setAuthorities(authorityService.findByNameIn(Constants.ROLE_USER));
        userService.save(user2);

        createArticle(adminUser, "Admin's First Article", "Content1 Admin");
        createArticle(adminUser, "Admin's Second Article", "Content2 Admin");
        createArticle(user1, "User1 Article", "Content User 1");
        createArticle(user2, "User2 Article", "Content User 2");
    }

    void createArticle(AppUser user, String title, String content) {
        var n = new Article();
        n.setCreatedByUser(user);
        n.setTitle(title);
        n.setContent(content);

        articleRepository.save(n);

        Comment c = new Comment();
        c.setStatus(CommentStatus.SHOWING);
        c.setContent("Test comment for " + title);
        c.setArticleId(n.getId());
        c.setCreatedByUser(user); //self
        commentRepository.save(c);

    }


}
