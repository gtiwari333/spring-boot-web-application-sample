package gt.app.modules.article;

import gt.app.domain.CommentStatus;

interface CommentRepositoryCustom {

    long findFlaggedComments(CommentStatus status);
}
