package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import org.jooq.Record7;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

interface ArticleRepositoryCustom {

    Page<Record7<Long, String, String, String, String, String, LocalDateTime>> testJooqPaginationQuery(Pageable pageable, UUID userId, ArticleStatus status);

    List<Article> findArticles(ArticleStatus status);

    long countArticles(ArticleStatus status);
}
