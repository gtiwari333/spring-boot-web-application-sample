package gt.app.modules.article;

import gt.app.domain.ArticleStatus;

interface ArticleRepositoryCustom {

    long countArticles(ArticleStatus status);
}
