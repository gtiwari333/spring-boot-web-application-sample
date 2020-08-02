package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.modules.common.AbstractRepository;

import java.util.List;

interface CommentRepository extends AbstractRepository<Comment>, CommentRepositoryCustom {

    List<Comment> findAllByArticleId(Long id);
}
