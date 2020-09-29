package gt.app.modules.article;

import com.querydsl.jpa.JPAExpressions;
import gt.app.domain.*;
import gt.app.modules.common.AbstractRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

import static gt.app.domain.QComment.comment;
import static gtapp.jooq.tables.GComment.G_COMMENT;

@Slf4j
class CommentRepositoryCustomImpl extends AbstractRepositoryImpl<Comment, CommentRepository> implements CommentRepositoryCustom {

    private final DSLContext jooq;

    public CommentRepositoryCustomImpl(DSLContext jooq) {
        super(Comment.class);
        this.jooq = jooq;
    }

    @Autowired
    @Lazy
    public void setRepository(CommentRepository repository) {
        this.repository = repository;
    }

    public void doTestQuery() {
        QArticle qArticle = QArticle.article;
        QUser user = QUser.user;

        //find articles that have length > 15 and the users with more than 5 articles

        var subquery1 = JPAExpressions
            .select(qArticle.createdByUser.id)
            .from(qArticle)
            .groupBy(qArticle.createdByUser.id)
            .having(qArticle.id.count().gt(5));

        var exp = user.id.in(subquery1);

        var exp2 = qArticle.title.length().lt(15);

        List<Article> articles = from(qArticle)
            .join(user).on(qArticle.createdByUser.id.eq(user.id))
            .select(qArticle)
            .where(exp.and(exp2))
            .fetch();

        log.debug("All Articles {} ", articles);
    }


    @Override
    public List<Comment> findComments(CommentStatus status) {
        return from(comment)
            .where(comment.status.eq(status))
            .fetch();
    }

    @Override
    public long findFlaggedComments(CommentStatus status) {
        return jooq.fetchCount(G_COMMENT.where(G_COMMENT.STATUS.eq(status.name())));
    }
}
