package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.modules.common.AbstractRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

import static gt.app.domain.QArticle.article;
import static gtapp.jooq.tables.GArticle.G_ARTICLE;

@Slf4j
class ArticleRepositoryCustomImpl extends AbstractRepositoryImpl<Article, ArticleRepository> implements ArticleRepositoryCustom {

    private final DSLContext jooq;

    ArticleRepositoryCustomImpl(DSLContext jooq) {
        super(Article.class);
        this.jooq = jooq;
    }

    @Autowired
    @Lazy
    public void setRepository(ArticleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Article> findArticles(ArticleStatus status) {
        return from(article)
            .where(article.status.eq(status))
            .fetch();
    }

    @Override
    public long countArticles(ArticleStatus status) {
        return jooq.fetchCount(G_ARTICLE.where(G_ARTICLE.STATUS.eq(status.name())));
    }
}
