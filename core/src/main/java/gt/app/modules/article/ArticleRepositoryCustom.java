package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;

import java.util.List;

interface ArticleRepositoryCustom {

    List<Article> findArticles(ArticleStatus status);

    long countArticles(ArticleStatus status);
}
