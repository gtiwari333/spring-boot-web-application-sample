package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.domain.CommentStatus;
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
