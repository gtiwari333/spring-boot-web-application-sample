package gt.app.modules.user;

import gt.app.config.security.CurrentUserToken;
import gt.app.domain.Article;
import gt.app.modules.article.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("appSecurity")
@RequiredArgsConstructor
@Slf4j
public class UserAuthorityService {

    private final ArticleService articleService;

    public boolean hasAccess(CurrentUserToken curUser, Long id, String entity) {

        if (curUser.isAdmin()) {
            return true;
        }


        if (Article.class.getSimpleName().equalsIgnoreCase(entity)) {

            UUID createdById = articleService.findCreatedByUserIdById(id);

            return createdById.equals(curUser.getUserId());
        }


        /*
        add more rules
         */

        return false;
    }

}
