package gt.app.modules.article;

import gt.app.domain.Article;
import gt.app.domain.ArticleStatus;
import gt.app.modules.common.AbstractRepositoryImpl;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record7;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static gt.app.domain.QArticle.article;
import static gtapp.jooq.tables.GArticle.G_ARTICLE;
import static gtapp.jooq.tables.GUser.G_USER;

class ArticleRepositoryCustomImpl extends AbstractRepositoryImpl<Article, ArticleRepository> implements ArticleRepositoryCustom {

    private final DSLContext jooq;

    public ArticleRepositoryCustomImpl(DSLContext jooq) {
        super(Article.class);
        this.jooq = jooq;
    }

    @Autowired
    @Lazy
    public void setRepository(ArticleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Record7<Long, String, String, String, String, String, LocalDateTime>> testJooqPaginationQuery(Pageable pageable, UUID userId, ArticleStatus status) {
        Condition where = G_ARTICLE.STATUS.eq(status.name());
        if (userId != null) {
            where.and(G_ARTICLE.CREATED_BY_USER_ID.eq(userId.toString()));
        }

        SelectConditionStep<org.jooq.Record> countQuery = jooq
            .select()
            .from(G_ARTICLE)
            .where(where); //FIXME: handle enum conversion for @Enumerated in JOOQ

        long count = jooq.fetchCount(countQuery);
        if (count == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        //count>0 -> fetch records
        var result = jooq
            .select(G_ARTICLE.ID, G_ARTICLE.TITLE, G_ARTICLE.STATUS, DSL.left(G_ARTICLE.CONTENT, 100), G_ARTICLE.CREATED_BY_USER_ID, G_USER.ID, G_ARTICLE.CREATED_DATE)
            .from(G_ARTICLE)
            .leftOuterJoin(G_USER).on(G_ARTICLE.CREATED_BY_USER_ID.eq(G_USER.ID))
            .where(where)
            .orderBy(getSortFields(getJooqSort(pageable.getSort()), G_ARTICLE))
            .limit(pageable.getPageSize()).offset(pageable.getOffset()) //CONFIRM if pagination is working
            .fetch();

        return new PageImpl<>(result, pageable, result.size());
    }

    //there might be better way of doing this
    Sort getJooqSort(Sort jpaSort) {

        if (jpaSort == null || jpaSort.isEmpty()) {
            return Sort.unsorted();
        }

        Iterator<Sort.Order> jpaFlds = jpaSort.iterator();

        List<Sort.Order> orders = new ArrayList<>();

        while (jpaFlds.hasNext()) {
            Sort.Order curJpaFld = jpaFlds.next();

            String sortFieldName = curJpaFld.getProperty();

            if ("createdDate".equalsIgnoreCase(sortFieldName)) {
                orders.add(new Sort.Order(curJpaFld.getDirection(), G_ARTICLE.CREATED_DATE.getName()));
            }

            if ("id".equalsIgnoreCase(sortFieldName)) {
                orders.add(new Sort.Order(curJpaFld.getDirection(), G_ARTICLE.ID.getName()));
            }

        }

        return Sort.by(orders);
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
