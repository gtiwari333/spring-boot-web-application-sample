package gt.app.modules.article;

import gt.app.domain.Comment;
import gt.app.modules.common.AbstractRepository;

import java.util.List;
import java.util.Optional;

interface CommentRepository extends AbstractRepository<Comment>, CommentRepositoryCustom {

    List<Comment> findAllByArticleId(Long id);

    boolean existsByIdAndArticleId(Long id, Long articleId);
}
