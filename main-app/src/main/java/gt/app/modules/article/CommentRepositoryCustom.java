package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.domain.CommentStatus;

import java.util.List;

interface CommentRepositoryCustom {

    List<Comment> findComments(CommentStatus status);

    long findFlaggedComments(CommentStatus status);
}
